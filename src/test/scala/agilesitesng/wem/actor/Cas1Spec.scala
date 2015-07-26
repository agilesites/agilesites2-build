package agilesitesng.wem.actor

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{TestActorRef, TestKit}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by msciab on 25/04/15.
 */
class Cas1Spec extends TestKit(ActorSystem("sbt-web", ConfigFactory.load().getConfig("sbt-web")))
with WordSpecLike
with MustMatchers
with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)

  import Cas1._
  import Protocol._

  val cas = TestActorRef[Cas1Actor]

  "cas" in {
    implicit val timeout = Timeout(3.second)

    val url = Some(new java.net.URL("http://localhost:11800/cs"))
    val user = Some("fwadmin")
    val pass = Some("xceladmin")

    val f = cas ? Connect(url, user, pass, "1")
    val Ticket(ticket) = Await.result(f, 3.second).asInstanceOf[Ticket]
    info(ticket)

  }
}