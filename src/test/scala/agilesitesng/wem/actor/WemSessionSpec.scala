package agilesitesng.wem.actor

import java.net.URL

import agilesitesng.wem.actor.Cas1.Cas1Actor
import agilesitesng.wem.actor.Cas3.Cas3Actor
import agilesitesng.wem.actor.Protocol.{WemGet, Session, Connect, Ticket}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{TestActorRef, TestKit}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import net.liftweb.json._
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by msciab on 25/04/15.
 */
class WemSessionSpec
  extends TestKit(ActorSystem("sbt-web", ConfigFactory.load().getConfig("sbt-web")))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {
  override def afterAll = TestKit.shutdownActorSystem(system)

   implicit val timeout = Timeout(3.second)
  val url = Some(new URL("http://localhost:7001/cs"))
  val cas = TestActorRef[Cas3Actor]
  val f = cas ? Connect(url, Some("fwadmin"), Some("xceladmin"), "3")
  val Session(session) = Await.result(f, 3.second).asInstanceOf[Session]

  "wem" in {
    val wem = TestActorRef(WemSession.actor(session.get, url))
    wem ! WemGet(testActor, "/sites")
    expectMsgPF(5.second) {
      case Protocol.Reply(json) => info(pretty(render(json)))
    }
  }
}