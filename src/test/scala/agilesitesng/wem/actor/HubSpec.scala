package agilesitesng.wem.actor

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{TestActorRef, TestKit}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._
import net.liftweb.json._

/**
 * Created by msciab on 25/04/15.
 */
class HubSpec
  extends TestKit(ActorSystem("sbt-web", ConfigFactory.load().getConfig("sbt-web")))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)

  import Hub._
  import Protocol._

  val hub = TestActorRef[HubActor]
  val url1 = Some(new java.net.URL("http://localhost:11800/cs"))
  val url3 = Some(new java.net.URL("http://localhost:7001/cs"))
  var user = Some("fwadmin")
  val pass = Some("xceladmin")

  val s3 = 3.second
  implicit val timeout = Timeout(s3)


  "hub1" in {

    val f = hub ? Connect(url1, user, pass, "1")
    Await.result(f, s3) === Status(OK)

    val Reply(json) = Await.result(hub ? Get("/sites"), s3)
    info(pretty(render(json)))

    Await.result(hub ? Disconnect(), s3) === Status(OK)

  }

  "hub3" in {

    val f = hub ? Connect(url3, user, pass, "3")
    Await.result(f, s3) === Status(OK)

    val Reply(json) = Await.result(hub ? Get("/sites"), s3)
    info(pretty(render(json)))

    Await.result(hub ? Disconnect(), s3) === Status(OK)

  }

}