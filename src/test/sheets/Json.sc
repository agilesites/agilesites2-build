import argonaut._, Argonaut._


case class PersonAge(intValue: Int)
case class Person(name: String, age: PersonAge)

object Person {
  implicit def PersonCodecJson: CodecJson[Person] =
  casecodec2(Person.apply, Person.unapply)("name", "age" )
}
object PersonAge {
  implicit def PersonAgeCodecJson: CodecJson[PersonAge] =
  casecodec1(PersonAge.apply, PersonAge.unapply)("intValue")
}
val p = Person("mike", PersonAge(47))
p.asJson

//ISO8601.parse("2015-06-20T15:59:00.150Z")

