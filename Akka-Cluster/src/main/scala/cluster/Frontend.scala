package cluster

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import com.typesafe.config.ConfigFactory
import common.{Add, BackendRegistration}

import scala.util.Random

/**
  * cluster.Frontend node actor
  */
class Frontend extends Actor {

  var backendNodes = IndexedSeq.empty[ActorRef]

  override def receive: Receive = {
    case Add if backendNodes.isEmpty =>
      /**
        * if backend cluster is empty then print error
        */
      println("[ERROR-FRONTEND]service unavailable, cluster doesn't have backend node")
    case addOp: Add if backendNodes.nonEmpty =>
      /**
        * multiple backend nodes in cluster then forward request to any of node
        */
      println("[INFO-FRONTEND] I will forward add operation to backend node to handle it.")
      backendNodes(Random.nextInt(backendNodes.size)).forward(addOp)
    case BackendRegistration if !backendNodes.contains(sender()) =>
      /**
        * when frontend receives the backend register message for a new backend node then store the node
        */
      backendNodes = backendNodes :+ sender()
      /**
        * start watching the node
        */
      context.watch(sender())
    case Terminated(backendNode: ActorRef) =>
      println(s"[INFO-FRONTEND] backend node : ${backendNode} is offline. UnWatching it.")
      /**
        * If any backend node terminated, remove from the backend list
        */
      backendNodes = backendNodes.filterNot(_ == backendNode)
      /**
        * unwatch the node
        */
      context.unwatch(backendNode)
  }
}

object Frontend {

  private var _frontend: ActorRef = _

  def initiate() = {

    val config = ConfigFactory.load().getConfig("Frontend")

    val system = ActorSystem("ClusterSystem", config)

    _frontend = system.actorOf(Props[Frontend], name = "frontend")
  }

  def getFrontEnd = _frontend

}