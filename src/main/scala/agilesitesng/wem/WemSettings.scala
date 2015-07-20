package agilesitesng.wem

import agilesites.Utils
import agilesitesng.wem.Protocol.{Reply, Put}
import agilesitesng.wem.model.{WemModel, CSElement}
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import sbt.Keys._
import sbt._

import scala.concurrent.Await
import scala.concurrent.duration._
import net.liftweb.json._

/**
 * Created by msciab on 01/07/15.
 */
trait WemSettings {
  this: AutoPlugin with Utils =>

  import AgileSitesWemKeys._

  implicit val timeout = Timeout(3.second)

  def process(ref: ActorRef, action: Symbol, args: Seq[String], log: Logger): String = {

    log.debug(s"${action.name}: ${args.mkString(" ")}")

    import Protocol._
    val inFile = """^<(.*)$""".r
    val outFile = """^>(.*)$""".r
    val req = """^(/.*)$""".r

    val m = args.map(_ match {
      case inFile(filename) => 'in -> readFile(file(filename))
      case outFile(filename) => 'out -> filename
      case req(arg) => 'arg -> arg
      case arg => println(s"ignored ${arg}")
        'ignore -> arg
    }).toMap

    log.debug(s"parsed: ${m.toString}")

    val arg = m.get('arg)
    if (arg.isEmpty) {
      s"""{ "error": "no args" }"""
    } else {
      val msg = action match {
        case 'get => Get(arg.getOrElse(""))
        case 'delete => Delete(arg.getOrElse(""))
        case 'post => Post(arg.getOrElse(""), parse(m.getOrElse('in, "")))
        case 'put => Put(arg.getOrElse(""), parse(m.getOrElse('in, "")))
      }

      log.debug(">>> sending " + msg.toString)
      val rf = ref ? msg
      val Reply(json) = Await.result(rf, 3.second).asInstanceOf[Reply]
      val res = pretty(render(json))
      log.debug("<<< received " + res)
      val out = m.get('out)
      if (out.isEmpty) {
        println(res)
      } else {
        writeFile(file(out.get), res, null)
        println(s"+++ ${out.get}")
      }
      res
    }
  }

  val wem = config("wem")

  def getTask = get in wem := {
    val log = streams.value.log
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    process(ref, 'get, args, log)
  }

  def postTask = post in wem := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    process(ref, 'post, args, streams.value.log)
  }

  def putTask = put in wem := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    process(ref, 'put, args, streams.value.log)
  }

  def deleteTask = delete in wem := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    process(ref, 'delete, args, streams.value.log)
  }

  def testCselement(id: Long, sitename: String, log: Logger): JValue = {
    import WemModel._
    val test = CSElement(id, "Test", blobFromFile("Test.groovy"))(sitename)
    import Serialization.{read, writePretty}
    implicit val formats = Serialization.formats(NoTypeHints)
    val pretty = writePretty(test)
    log.info(pretty)
    parse(pretty)
  }

  def setupTask = setup in wem := {
    import agilesites.config.AgileSitesConfigKeys._

    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    val log = streams.value.log

    val sitename = sitesFocus.value
    val id = 10000l

    val asset = testCselement(id, sitename, log)

    val cmd = s"/sites/${sitename}/types/CSElement/assets/${id}"
    val msg = Put(cmd, asset)

    log.debug(">>> sending " + msg.toString)
    val res = ref ? msg
    val Reply(json) = Await.result(res, 3.second).asInstanceOf[Reply]
    log.debug("<<< received " + res)

    "setup: OK"
  }

  val wemSettings = Seq(
    ivyConfigurations += wem,
    getTask, postTask, putTask, deleteTask, setupTask)

}
