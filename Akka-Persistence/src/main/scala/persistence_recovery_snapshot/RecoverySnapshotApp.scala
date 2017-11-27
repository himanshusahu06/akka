package persistence_recovery_snapshot

import akka.actor.{ActorSystem, Props}
import persistence_recovery_snapshot.Counter.{Cmd, Decrement, Increment}

object RecoverySnapshotApp extends App {

  val system = ActorSystem("persisted-recovery-snapshot-actor")

  val persistedActor = system.actorOf(Props[Counter], "persisted-recovery-snapshot-counter-actor")

  persistedActor.!(Cmd(Increment(3)))

  persistedActor.!(Cmd(Increment(5)))

  persistedActor.!(Cmd(Decrement(3)))

  persistedActor.!("print")

  Thread.sleep(1000)

  system.terminate()
}