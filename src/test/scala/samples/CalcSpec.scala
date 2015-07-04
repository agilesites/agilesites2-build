package samples

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{Ignore, BeforeAndAfterAll, MustMatchers, WordSpecLike}

/**
 * Created by msciab on 08/04/15.
 */
@Ignore class CalcSpec extends TestKit(ActorSystem("Calc"))
with WordSpecLike with MustMatchers with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)

  import Calc._

  "A Calc actor" must {

    //val calc = system.actorOf(Props[CalcActor], "calc")
    val calc = TestActorRef[CalcActor]
    val act = calc.underlyingActor

    "return sum" in {

      calc ! Num(1)

      assert(act.stack.length == 1)
      assert(act.stack.head == 1)

      calc ! Num(2)
      calc ! Sum(testActor)

      assert(act.stack.size == 0)

      expectMsg(Num(3))
    }

  }


}
