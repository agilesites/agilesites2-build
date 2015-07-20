package agilesitesng.wem

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.duration._

/**
 * Created by msciab on 25/04/15.
 */
class WemSpec
  extends TestKit(ActorSystem("sbt-web", ConfigFactory.load().getConfig("sbt-web")))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)


  val wem = TestActorRef(Wem.wemActor())

  "wem" in {

    wem ! Wem.AskGet(testActor, "/users")

    expectMsgPF(5.second) {
      case Protocol.Reply(json) => info(json.toString)
    }
  }
}