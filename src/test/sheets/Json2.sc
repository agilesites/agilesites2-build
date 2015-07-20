import argonaut._, Argonaut._, scalaz._, Scalaz._

case class Top(name: String, attrs: List[String], age: Option[Integer] = None)

object Top {
  // standard automated
  /*
  implicit def CodecTop: CodecJson[Top]
    = casecodec2(Top.apply, Top.unapply)("name", "attrs")
  */
  implicit def EncodeTop: EncodeJson[Top] =
  /* Encoder
  EncodeJson { top =>
    Json("name" -> jString(top.name),
      "attrs" -> jArray(top.attrs.map(jString(_))))
  }*/
    EncodeJson { top =>
      ("attrs" := top.attrs) ->:
        ("name" := top.name) ->:
        ("age" :=? top.age) ->?:
        jEmptyObject
    }
/*
  implicit def DecodeTop: DecodeJson[Top] =
  DecodeJson { c =>

  }*/
}

val mike = Top("mike", List("max", "laura")).asJson.toString

val miri = Top("miri", List("max", "laura"), age=Some(49)).asJson.toString

//val opt = Json("a" -> jString("1"), "b" -> None)

val mkc = Parse.parse(mike).toOption.get.cursor

val mrc = Parse.parse(miri).toOption.get.cursor


mkc.downField("name").map(_.focus)
mrc.downField("name").map(_.focus)

mkc.downField("age").map(_.focus)
mrc.downField("age").map(_.focus)


// (mc --\ "name").get

/*
for {
  name <- mkc.downField("name").as[String]
  age <- mkc.downField("age").as[Integer]
  attrs <- mkc.downField("attrs").as[List[String]]
} yield Top(name, attrs, age)
*/


//field("name").get.focus
//val miriJson = Parse.parse(miri).toOption.get
//val cvt = jObjectPL >=>
//jsonObjectPL("age") >=>
//  jObjectPL >=> jNumberPL
//cvt.get(miriJson)
