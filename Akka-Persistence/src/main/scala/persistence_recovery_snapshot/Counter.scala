package persistence_recovery_snapshot

import akka.actor.ActorLogging
import akka.persistence._

object Counter {
  sealed trait Operation {
    val count: Int
  }
  case class Increment(override val count: Int) extends Operation
  case class Decrement(override val count: Int) extends Operation

  // Command (Cmd) - operation from outside world
  case class Cmd(op: Operation)
  // Event (Evt) - stored operation from journal
  case class Evt(op:Operation)

  case class State(count: Int)
}

/**
  * Persisted Actor with snapshot
  */
class Counter extends PersistentActor with ActorLogging {

  import Counter._

  // Persistent Identifier
  override def persistenceId = "counter-persistence-id"

  // state to be updated
  var state: State = State(count = 0)

  // update internal state of the persisted actor
  def updateState(evt: Evt): Unit = evt match {
    case Evt(Increment(count)) =>
      state = State(count = state.count + count)
      takeSnapshot
    case Evt(Decrement(count)) =>
      state = State(count = state.count - count)
      takeSnapshot
  }

  // Persistent received on recovery mode and a persistence actor starts with recovery mode
  override def receiveRecover: Receive = {
    case evt: Evt =>
      println(s"Counter receive ${evt} on recovering mode")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"Counter receive snapshot with data : ${snapshot} on recovering mode")
      state = snapshot
    case RecoveryCompleted =>
      println("--->[INFO] Recovery Completed... Switching to receive command mode.")
  }

  // Persistent received on normal mode
  override def  receiveCommand: Receive = {
    case cmd:Cmd =>
      println(s"Counter receive ${cmd}")
      // persist the event and if state persistence succeeded then update the state
      persist(Evt(cmd.op)) {
        evt =>
          updateState(evt)
      }
    case "print" =>
      println(s"Current state of counter is ${state}")
    case SaveSnapshotSuccess(metadata) =>
      println("--->[INFO] Save snapshot succeeded.")
    case SaveSnapshotFailure(metadata, reason) =>
      println(s"--->[ERROR] Save snapshot failed and failure is : ${reason}")
  }

  // take snapshot when state counter is multiple of 5
  def takeSnapshot = {
    if (state.count % 5 == 0) {
      saveSnapshot(state)
    }
  }

  // do not recover
  // override def recovery: Recovery = Recovery.none
}
