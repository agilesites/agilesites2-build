import agilesitesng.wem.model.CSElement
import agilesitesng.wem.model.WemModel._

import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
implicit val formats = Serialization.formats(NoTypeHints)

val cs = CSElement(1000l, "test", blobFromFile("/etc/hosts"))("AdminSite")

val ser = write(cs)
var json = parse(ser)

println(pretty(render(json)))