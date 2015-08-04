package samples

/**
 * Created by msciab on 14/04/15.
 */

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{Ignore, BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.duration._

/**
 * Created by msciab on 08/04/15.
 */
@Ignore class IpSpec extends TestKit(ActorSystem("Ip"))
with WordSpecLike with MustMatchers with BeforeAndAfterAll {

  override def afterAll = {
    Thread.sleep(1000) // horrible hack
    TestKit.shutdownActorSystem(system)
  }

  import Ip._


  "A Ip actor" must {
    val http = IO(Http)

    val ip = TestActorRef[IpActor]
    //val act = ip.underlyingActor

    "return ip" in {

      ip ! IpInit(http)

      ip ! IpRequest(testActor)

      val msg = expectMsgPF(10.second) {
        case IpResponse(res) =>
          info(s"got ${res}")
          res
      }

      assert(msg.indexOf("ip") != -1)

      //http ! PoisonPill
    }
  }


}
