package agilesitesng.wem.model

import org.scalatest._
import net.liftweb.json._

class WemModelSpec extends FreeSpec {

  "serialize properly" in {
    import WemModel._

    val blob =  blobFromFile("src/test/resources/Test.jsp")

    val cs = CSElement(1, "test", blob )("AdminSite")

    import Serialization.{read,write}
    implicit val fmt = Serialization.formats(NoTypeHints)

    val wr = Serialization.write(cs)(fmt)
    println(wr)

    val ser = parse(wr)
    val out = pretty(render(ser))

    println(out)

    val back = Serialization.read[Asset](out)

    println(back)
  }
}
