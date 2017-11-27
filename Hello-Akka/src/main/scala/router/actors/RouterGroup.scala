package router.actors

import akka.actor.{Actor, ActorRef}
import router.actors.Worker.Job

/**
  * Router Group that takes list of router path as a parameter and assign job using actor selection
  * @param routes
  */
class RouterGroup(routes: List[String]) extends Actor {

  override def receive = {
    case msg:Job =>
      println("I am a router and I received a Job to execute.....")
      context.actorSelection(routes(util.Random.nextInt(routes.size))).forward(msg)
  }
}
