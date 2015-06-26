package agilesitesng.wem

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.duration._

/**
 * Created by msciab on 25/04/15.
 */
class WemSpec
  extends TestKit(ActorSystem("Wem"))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)

  import agilesitesng.wem.Wem._

  val wem = TestActorRef(wemActor())

  "wem" in {

    wem ! AskGet(testActor, "/users")

    expectMsgPF(5.second) {
      case Reply(json) => info(json.spaces2)
    }
  }
}