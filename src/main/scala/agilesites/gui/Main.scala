package agilesites.gui

import scalafx.application.JFXApp

object Main extends JFXApp {

  val cmd = parameters.raw.headOption.getOrElse("help")

  stage =  cmd match {

    case "download" =>

      val arg2 = parameters.raw.tail.headOption
      new DownloaderStage(arg2.getOrElse(System.getProperty("user.dir")))

    case "message" =>

      val message = parameters.raw.tail.mkString("\n")
      new InfoStage(message)

    case "help" => new InfoStage(
      """
        |Usage:
        |
        |    help              this screen
        |    download          download WebCenter Sites
      """.stripMargin)

    //case "Worker" => WorkerStage

    case x => new InfoStage(s"command '${x}' not recognized")
  }


}