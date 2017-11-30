package load_balancing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import common.Add
import scala.concurrent.duration._
import scala.util.Random

class Frontend extends Actor {

  import context.dispatcher

  val backend = context.actorOf(FromConfig.props(), name = "backendRouter")

  // just send message to frontend in a regular interval
  context.system.scheduler.schedule(3.seconds, 3.seconds, self, Add(Random.nextInt(100), Random.nextInt(100)))

  override def receive: Receive = {
    case addOp: Add =>
      println(s"[INFO-FRONTEND] I'll forward add operation to backend node to handle it.")
      backend.forward(addOp)
  }
}

object Frontend {

  private var _frontend: ActorRef = _

  def initiate() = {

    val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]")
      .withFallback(ConfigFactory.load("loadbalancer"))

    val system = ActorSystem("ClusterSystem", config)

    //#registerOnUp
    Cluster(system).registerOnMemberUp(
      _frontend = system.actorOf(Props[Frontend], name = "frontend")
    )
  }

  def getFrontEnd = _frontend

}