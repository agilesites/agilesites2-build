package agilesitesng.wem

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask

/**
 * Created by msciab on 25/04/15.
 */
class HubSpec extends TestKit(ActorSystem("Wem")) with WordSpecLike with MustMatchers with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)

  import agilesitesng.wem.Hub._
  import agilesitesng.wem.Wem._

  val hub = TestActorRef[HubActor]

  hub

  "hub" in {
    implicit val timeout = Timeout(3.second)

    val f = hub ? Init()

    f.value.get.get  === "OK"

    println("ok")

    val rf = hub ? Get("/users")

    val Reply(json) = Await.result(rf, 3.second).asInstanceOf[Reply]

    info(json.spaces2)

    //println("Get /users")

    /*expectMsgPF(1 second) {
     case Reply(json) => info("json!!!")
   }*/

  }
}