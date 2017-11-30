package load_balancing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import common.Add

class Backend extends Actor {

  override def receive: Receive = {
    case Add(_, _) =>
      println(s"[INFO-BACKEND] I'm backend with path: ${self} and I just received add operation")
  }
}

object Backend {

  def initiate(port: Int): ActorRef = {

    val config = ConfigFactory
      .parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(ConfigFactory.load("loadbalancer"))

    println(config.toString)

    val system = ActorSystem("ClusterSystem", config)

    system.actorOf(Props[Backend], name = "backend")
  }
}