package samples

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest._

import scala.concurrent.duration._

/**
 * Created by msciab on 12/04/15.
 */
class LoginSpec extends TestKit(ActorSystem("Login")) with WordSpecLike with MustMatchers with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)

  import Login._

  "login mock" in {
    val login = TestActorRef[MockLoginActor]

    login ! DoLogin(testActor, Some(""), "test", "test")

    expectMsg(Ticket(Some("12345")))

    login ! AskTicket(testActor)

    expectMsg(Ticket(Some("12345")))

    login ! DoLogin(testActor, Some(""), "test", "test1")

    expectMsg(Ticket(None))

  }

  "login with dispatch" in {
    val login = TestActorRef[DispatchLoginActor]

    login ! DoLogin(testActor)

    val tk = expectMsgPF(5.second) {
      case Ticket(Some(x)) => x
    }

    info(tk)


    login ! AskTicket(testActor)

    tk === expectMsgPF(1.second) {
      case Ticket(Some(x)) => x
    }

  }

  "login with spray" in {


    val login = TestActorRef[LoginActor]

    login ! DoLogin(testActor)

    //login ! SendTicket(testActor)

    val tk = expectMsgPF(5.second) {
      case Ticket(Some(x)) =>
        info(s"got ticket $x")
        x
    }

    login ! AskTicket(testActor)

    tk === expectMsgPF(1.second) {
      case Ticket(Some(x)) => x
    }
  }

}


