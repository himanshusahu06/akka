package router.actors

import akka.actor.{Actor, ActorRef, Props}
import router.actors.Worker.Job

/**
  * Simple router that routes the message to other actors
  *
  * forward => Forwards the message and passes the original sender actor as the sender.
  */
class Router extends Actor {

  var routes:List[ActorRef] = _

  override def preStart() = {
    routes = List.fill(5)(
      context.actorOf(Props[Worker])
    )
  }

  override def receive = {
    case msg:Job =>
      println("I am a router and I received a Job to execute.....")
      routes(util.Random.nextInt(routes.size)) forward(msg)
  }
}