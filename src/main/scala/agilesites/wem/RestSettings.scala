package agilesites.wem

import sbt._
import sbt.Keys._
import agilesites.build._
import dispatch._
import dispatch.Defaults._

trait RestSettings {
  this: Plugin with ConfigSettings =>

  // Keys
  lazy val wemConfig = config("wem").hide
  lazy val login = taskKey[String]("WEM login")
  lazy val get = inputKey[Unit]("WEM get")

  // Tasks
  val getTask = get := {
    val args = Def.spaceDelimited("<args>").parsed
    if (args.isEmpty) {
      println("usage: wem:get path [xpath...]")
    } else {
      val token = login.value
      val base = sitesUrl.value
      val s = RestUtil.get(base, args.head, token)
      RestUtil.processXmlWithXpath(s, args.tail)
      //println(RestUtil.prettyPrintXml(s))
    }
  }

  val loginTask = login := {
    val url = sitesUrl.value
    val user = sitesUser.value
    val pass = sitesPassword.value
    RestUtil.login(url, user, pass)
  }
  
  //
  val restSettings = Seq(ivyConfigurations += wemConfig) ++
    inConfig(wemConfig) {
      Seq(loginTask, getTask)
    }

}