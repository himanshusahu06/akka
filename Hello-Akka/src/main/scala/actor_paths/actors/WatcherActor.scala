package actor_paths.actors

import akka.actor.{Actor, ActorIdentity, ActorRef, Identify}

/**
  * Simple Watcher actor that will fetch ActorRef from actor selection using unique actor path
  */
class WatcherActor extends Actor {

  var counterRef:ActorRef = _

  val selection = context.actorSelection("/user/counter")

  selection.!(Identify(None))

  override def receive = {
    case ActorIdentity(_, Some(ref)) =>
      println(s"Actor reference for counter is : ${ref}")
      counterRef = ref
    case ActorIdentity(_, None) =>
      println(s"Actor selection for doesn't live")
  }
}

