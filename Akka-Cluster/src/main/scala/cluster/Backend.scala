package cluster

import akka.actor.{Actor, ActorRef, ActorSystem, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import com.typesafe.config.ConfigFactory
import common.{Add, BackendRegistration}

/**
  * cluster Backend Node Actor
  */
class Backend extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp and resubscribe when restart (domain events)
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    case Add(a,b) =>
      println(s"[INFO-BACKEND] I'm backend with path: ${self} and I just received add operation")
    case MemberUp(member) =>
      if (member.hasRole("frontend")) {
        context.actorSelection(RootActorPath(member.address) / "user" / "frontend").!(BackendRegistration)
      }
  }
}

object Backend {

  def initiate(port: Int): ActorRef = {

    val config = ConfigFactory
      .parseString(s"akka.remote.netty.tcp.port=${port}")
      .withFallback(ConfigFactory.load().getConfig("Backend"))

    val system = ActorSystem("ClusterSystem", config)

    system.actorOf(Props[Backend], name = "Backend")
  }
}