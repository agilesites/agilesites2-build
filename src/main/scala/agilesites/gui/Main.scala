package agilesites.gui

import scalafx.application.JFXApp

object Main extends JFXApp {

  println(parameters.raw.headOption.getOrElse("hello"))
  
  stage = parameters.raw.headOption.getOrElse("help") match {
    
  	case "download" => DownloaderStage

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