package agilesitesng.deploy

import java.net.URLEncoder

import agilesites.config.AgileSitesConfigKeys._
import agilesitesng.Utils
import agilesitesng.deploy.actor.DeployProtocol._
import agilesitesng.deploy.model.Spooler
import akka.pattern.ask
import akka.util.Timeout
import sbt.{AutoPlugin, _}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by msciab on 04/08/15.
 */
trait SqlSettings {
  this: AutoPlugin with Utils =>

  import NgDeployKeys._

  def query(url: String, sql1: String, tables: String, limit: Int = -1)(username:String, password: String) = {
    val sql2 = URLEncoder.encode(sql1, "UTF-8")
    val req = s"${url}/ContentServer?pagename=AAAgileService&op=sql&sql=${sql2}&limit=${limit}&tables=${tables}&username=${username}&password=${password}"
    println(">>> " + req)
    val r = httpCallRaw(req)
    println(r)
    r
  }

  val selectTask = sqlSelect := {

    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val args1 = args.map(_.toLowerCase)
    if (args.size == 0 || args1.indexOf("from") == -1) {
      val msg = "usage: sqlSelect <fields> from <table> where <condition> [limit <n>]"
      println(msg)
      msg
    } else {
      val tables = args(args1.indexOf("from") + 1)
      val (sql, limit) = if (args1.init.last.toLowerCase() == "limit")
        (args.init.init, args.last.toInt)
      else
        (args, -1)

      val sql1 = sql.mkString("select ", " ", "");
      query(sitesUrl.value, sql1, tables, limit)(sitesUser.value, sitesPassword.value)
    }
  }


  val deleteTask = sqlDelete := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    if (args.size < 2) {
      val msg = "usage: sqlDelete from <table> [where <condition>]"
      println(msg)
      msg
    } else {
      val sql = args.mkString("delete ", " ", "");
      query(sitesUrl.value, sql, args(1))(sitesUser.value, sitesPassword.value)
    }
  }

  val insertTask = sqlInsert := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    if (args.size < 2) {
      val msg = "usage: sqlInsert into <table> <insert-body>"
      println(msg)
      msg
    } else {
      val sql = args.mkString("insert ", " ", "");
      query(sitesUrl.value, sql, args(1))(sitesUser.value, sitesPassword.value)
    }
  }

  val updateTask = sqlUpdate := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    if (args.size < 2) {
      val msg = "usage: sqlUpdate into <table> <insert-body>"
      println(msg)
      msg
    } else {
      val sql = args.mkString("update ", " ", "");
      query(sitesUrl.value, sql, args(0))(sitesUser.value, sitesPassword.value)
    }
  }

  def sqlSettings = Seq(selectTask, deleteTask, insertTask, updateTask)

}
