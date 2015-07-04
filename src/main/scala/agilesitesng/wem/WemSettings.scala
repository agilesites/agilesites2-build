package agilesitesng.wem

import agilesites.Utils
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import argonaut._
import sbt.Keys._
import sbt._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by msciab on 01/07/15.
 */
trait WemSettings {
  this: AutoPlugin with Utils =>

  import AgileSitesWemKeys._

  implicit val timeout = Timeout(3.second)

  def process(ref: ActorRef, action: Symbol, args: Seq[String], log: Logger): String = {

    import Protocol._
    val inFile = """^<(.*)$""".r
    val outFile = """^>(.*)$""".r
    val req = """^(/.*)$""".r

    val m = args.map(_ match {
      case inFile(filename)  => 'in -> readFile(file(filename))
      case outFile(filename) => 'out -> filename
      case req(arg)   => 'arg -> arg
      case arg => println(s"ignored ${arg}")
        'ignore -> arg
    }).toMap

    log.debug(m.toString)

    val arg = m.get('arg)
    if (arg.isEmpty) {
      s"""{ "error": "no args" }"""
    } else {
      val msg = action match {
        case 'get => Get(arg.getOrElse(""))
        case 'delete => Delete(arg.getOrElse(""))
        case 'post => Post(arg.getOrElse(""), Parse.parseOption(m.getOrElse('in, "")))
        case 'put => Put(arg.getOrElse(""), Parse.parseOption(m.getOrElse('in, "")))
      }

      log.debug(">>> sending "+msg.toString)
      val rf = ref ? msg
      val Reply(json) = Await.result(rf, 3.second).asInstanceOf[Reply]
      val res = json.spaces2
      log.debug("<<< received "+res)
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
    log.info(args.mkString(" "))
    process(ref, 'get, args, log)
  }

  def putTask = put in wem := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    process(ref, 'put, args, streams.value.log)
  }

  def postTask = post in wem := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    process(ref, 'delete, args, streams.value.log)
  }

  def deleteTask = delete in wem := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    process(ref, 'delete, args, streams.value.log)
  }

  val wemSettings = Seq(
    ivyConfigurations += wem,
    getTask, postTask, putTask, deleteTask)

}
