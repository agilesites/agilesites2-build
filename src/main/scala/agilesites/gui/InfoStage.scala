package agilesites.gui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene._
import scalafx.scene.layout._
import scalafx.scene.control._
import scalafx.scene.web.WebView
import scalafx.application.Platform
import scalafx.stage.Stage
import scalafx.scene.text.{FontWeight, Font}

import scalafx.application.JFXApp.PrimaryStage
import java.util.zip.ZipEntry

class InfoStage(message: String) extends PrimaryStage {

  title = "Info"
  resizable = false
  scene = new Scene(640, 480) {
    root = new BorderPane {
      center =  new Label {
        text = message
        font = Font.font("Courier", FontWeight.Bold, 16)
      }
      //bottom = createNavigation
    }
  }

  def createTitle = new Region {
    style = "-fx-background-color: radial-gradient(radius 100%, white, gray)"
  }

  def createNavigation = new HBox {
    //layoutX = 60
    //layoutY = 420
    spacing = 20
    style = "-fx-background-color: #336699;"
    children = List(
      new Button {
        text = "<< Prev"
        //onAction = handle { anim.playFromStart() }
        //disable <== anim.status =!= Status.STOPPED
      },
      new HBox {
        hgrow = Priority.Always
      },
      new Button {
        text = "Next >>"
        //onAction = handle { anim.pause() }
        //disable <== anim.status =!= Status.RUNNING
      })

  }
  //val status = new ProgressBar
  //val browser = new WebView
  //browser.disable = true
  //Platform.runL
  //browser.engine.getLoadWorker.cancel()
  //val ze: ZipEntry = new ZipEntry() 
  //ze.ge
}