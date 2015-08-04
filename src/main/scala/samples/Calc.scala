package samples

import akka.actor.{Actor, ActorRef}

object Calc {

  case class Num(n: Int)
  case class Sum(ref: ActorRef)

  class CalcActor extends Actor {

    var stack = List[Int]()

    def receive = {

      case Num(n) =>
        stack =  n :: stack

      case Sum(act) =>
        stack match {
          case a :: b :: rest =>
            stack = rest
            act ! Num(a+b)

          case _ => throw new Exception("not enough args in the stack")
        }
    }
  }
}
