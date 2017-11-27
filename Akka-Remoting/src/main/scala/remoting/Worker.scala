package remoting

import akka.actor.Actor

class Worker extends Actor {
  override def receive = {
    case msg:String =>
      println(s"received some work and actorRef: ${self}")
  }
}
