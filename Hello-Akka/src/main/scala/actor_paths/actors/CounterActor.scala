package actor_paths.actors

import akka.actor.Actor

/**
  * Counter actor
  */
class CounterActor extends Actor {

  var count:Int = 0

  override def receive = {
    case "INC" =>
      count+=1
    case "DEC" =>
      count-=2
  }
}