package persistence

import akka.actor.{ActorSystem, Props}
import persistence.Counter.{Cmd, Decrement, Increment}

object PersistenceApp extends App {

  val system = ActorSystem("persistent-actor")

  val persistedActor = system.actorOf(Props[CounterActor], "persisted-counter-actor")

  persistedActor.!(Cmd(Increment(3)))

  persistedActor.!(Cmd(Increment(5)))

  persistedActor.!(Cmd(Increment(2)))

  persistedActor.!(Cmd(Decrement(3)))

  persistedActor.!("print")

  Thread.sleep(1000)

  system.terminate()
}
