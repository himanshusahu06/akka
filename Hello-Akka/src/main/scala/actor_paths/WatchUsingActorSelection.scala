package actor_paths

import akka.actor.{ActorSystem, Props}

/**
  * actor reference discovery using actor selection
  */
object WatchUsingActorSelection extends App {

  val system:ActorSystem = ActorSystem.create("watch-actor-selection")

  val counter = system.actorOf(Props[CounterActor], "counter")

  val watch = system.actorOf(Props[WatcherActor], "watcher")

  Thread.sleep(1000)

  system.terminate()
}
