package persistence

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, SnapshotOffer}

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
  * Persisted Actor
  */
class CounterActor extends PersistentActor with ActorLogging {

  import Counter._

  // Persistent Identifier
  override def persistenceId = "counter-persistence-id"

  // state to be updated
  var state: State = State(count = 0)

  // update internal state of the persisted actor
  def updateState(evt: Evt): Unit = evt match {
    case Evt(Increment(count)) =>
      state = State(count = state.count + count)
    case Evt(Decrement(count)) =>
      state = State(count = state.count - count)
  }

  // Persistent received on recovery mode and a persistence actor starts with recovery mode
  override def receiveRecover: Receive = {
    case evt: Evt =>
      println(s"Counter receive ${evt} on recovering mode")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"Counter receive snapshot with data : ${snapshot} on recovering mode")
      state = snapshot
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
  }
}
