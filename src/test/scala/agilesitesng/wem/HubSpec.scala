package agilesitesng.wem

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask

/**
 * Created by msciab on 25/04/15.
 */
class HubSpec extends TestKit(ActorSystem("sbt-web", ConfigFactory.load().getConfig("sbt-web")))
with WordSpecLike
with MustMatchers
with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)


  import agilesitesng.wem.Hub._
  import agilesitesng.wem.Wem._
  import agilesitesng.wem.Protocol._

  val hub = TestActorRef[HubActor]

  "hub" in {
    implicit val timeout = Timeout(3.second)

    val url = Some(new java.net.URL("http://localhost:11800/cs"))
    var user = Some("fwadmin")
    val pass = Some("xceladmin")

    val f = hub ? Connect(url, user, pass)
    f.value.get.get === Status(OK)
    println("ok")

    val rf = hub ? Get("/sites")
    val Reply(json) = Await.result(rf, 3.second).asInstanceOf[Reply]
    info(json.toString)

    //val Reply(json2) = Await.result(hub ? Get("/sites"), 3.second).asInstanceOf[Reply]
    //info(json2.nospaces)
    /*expectMsgPF(1 second) {
     case Reply(json) => info("json!!!")
   }*/

  }
}