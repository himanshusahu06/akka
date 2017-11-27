package actor_paths

import actor_paths.actors.CounterActor
import akka.actor.{ActorRef, ActorSelection, ActorSystem, InvalidActorNameException, PoisonPill, Props}

/**
  * Actor ref will be unique for each actor
  *
  * An ActorSelection is a logical view of an ActorSystem's tree of Actors
  *
  * we can fetch actor ref from actor selection using unique actor name
  *
  * Simple Watcher actor that will fetch ActorRef from actor selection using unique actor path
  */
object ActorPath extends App {

    val system:ActorSystem = ActorSystem.create("ActorPath")

    var counterActor:ActorRef = _

    try {
      counterActor = system.actorOf(Props[CounterActor], "Counter")
    } catch {
      case ian: InvalidActorNameException =>
        println(s"[ERROR] Multiple actors for given name / no actor for given name ${ian}")
        System.exit(0)
      case ex:Exception =>
        println(s"[ERROR] Unknown exception ${ex}")
        System.exit(0)
    }

    val actorSelection:ActorSelection = system.actorSelection("Counter")

    println(s"Actor reference for counterActor: ${counterActor}")

    println(s"Actor selection for counterActor: ${actorSelection}")

    /**
    * A message all Actors will understand, that when processed will terminate the Actor permanently.
    */
    counterActor.!(PoisonPill)

    Thread.sleep(1000)

    var counterActor2:ActorRef = _

    try {
      counterActor2 = system.actorOf(Props[CounterActor], "Counter")
    } catch {
      case ian: InvalidActorNameException =>
        println(s"[ERROR] Multiple actors for given name / no actor for given name ${ian}")
        System.exit(0)
      case ex:Exception =>
        println(s"[ERROR] Unknown exception ${ex}")
        System.exit(0)
    }

    val actorSelection2:ActorSelection = system.actorSelection("Counter")

    println(s"Actor reference for counterActor2: ${counterActor2}")

    println(s"Actor selection for counterActor2: ${actorSelection2}")

    system.terminate()
}
