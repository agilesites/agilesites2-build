import java.util.Date

import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.{write}

parse( """ {"numbers": [1,2,3]}""")
val json =  ("name" -> "joe") ~ ("age" -> Some(35))
case class Blob(filename: String,
                size: Int,
                date: Date,
                opt: Option[String]=None)

val blob = Blob("file", 1000, new Date())


implicit val formats = Serialization.formats(NoTypeHints)

write(blob)
write(blob.copy(opt=Some("string")))