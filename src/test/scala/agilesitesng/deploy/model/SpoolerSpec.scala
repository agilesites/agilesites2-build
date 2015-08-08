package agilesitesng.deploy.model

import agilesitesng.deploy.model.DeployModel.{Template, Site, CSElement}
import net.liftweb.json.{ShortTypeHints, Serialization}
import net.liftweb.json.Serialization.{read, write}

import org.scalatest.{Matchers, FreeSpec}

class SpoolerSpec extends FreeSpec with Matchers {

  val s1 = Site(1l, "TestSite")
  val a2 = Template(2l, "TestTemplate")
  val a3 = CSElement(3l, "TestCSElement")

  "spool i/o" in {
    Spooler.insert(1, s1)
    Spooler.extract() === s1
    Spooler.extract() === None
  }

  "spool priority" in {
    Spooler.insert(2, a2)
    Spooler.insert(1, s1)
    Spooler.extract().get === s1
    Spooler.extract().get === a2
    Spooler.extract() === None
  }

  "spool priority multiple" in {
    Spooler.insert(2, a2)
    Spooler.insert(1, s1)
    Spooler.insert(2, a3)

    Spooler.extract() === s1
    Spooler.extract() === a2
    Spooler.extract() === s1
    Spooler.extract() === None
  }

  "export" in {
    Spooler.insert(1, s1)
    Spooler.insert(2, a3)
    Spooler.insert(3, a2)

    val s = Spooler.save

    info(s)

    val l = Spooler.load(s)

    info(l.toString)

    l(0) == a2
    l(1) == a3
    l(2) == s1

    //Spooler.insert(3, a2)
    //Spooler.load(r)
    //Spooler.extract() === Some(a3)
    //Spooler.extract() === Some(s1)
    //Spooler.extract() === None
  }

  "animals" in {
    trait Animal
    case class Dog(name: String) extends Animal
    case class Fish(weight: Double) extends Animal
    case class Animals(animals: List[Animal])
    implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Animals], classOf[Dog], classOf[Fish])))
    val ser = write(Animals(Dog("pluto") :: Fish(1.2) :: Nil))
    //info(read[Animals](ser))
    val deser = read[Animals](ser)
    info(deser.toString)
  }

}
