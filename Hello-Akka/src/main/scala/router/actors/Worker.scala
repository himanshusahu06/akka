package router.actors

import java.util.UUID

import akka.actor.Actor

/**
  * This actor handles Job messages
  */
class Worker extends Actor {

  import Worker._

  override def receive = {
    case job:Job =>
      println(s"I received a Job (${job}) message and my ActorRef: ${self} ")
  }
}

/**
  * Message object for Worker Actor
  */
object Worker {
  case class Job(id: UUID, name: String)
}