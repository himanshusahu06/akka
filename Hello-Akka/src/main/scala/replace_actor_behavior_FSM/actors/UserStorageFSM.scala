package replace_actor_behavior_FSM.actors

import akka.actor.{FSM, Stash}

/**
  * Actor that changes it's behavior on runtime.
  *
  * The Stash enables an actor to temporarily stash away messages that can not or
  * should not be handled using the actor's current behavior.
  */
class UserStorageFSM extends FSM[UserStorageFSM.State, UserStorageFSM.Data] with Stash {

  /**
    * 1. Set initial state of the actor (machine)
    * start with disconnected state and empty data
    */
  startWith(UserStorageFSM.Disconnected, UserStorageFSM.EmptyData)

  /**
    * 2.1 define state - disconnected state
    */
  when(UserStorageFSM.Disconnected) {
    // FSM receives events
    case Event(UserStorageFSM.Connect, _) =>
      println(s"User storage connected to DB")
      // Prepends all messages in the stash to the mailbox, and then clears the stash
      unstashAll()
      // goto : Produce transition to other state.
      // using: Modify state transition descriptor with new state data.
      goto(UserStorageFSM.Connected).using(UserStorageFSM.EmptyData)
    case Event(msg:UserStorageFSM.Operation,_) =>
      println(s"--->[INFO] adding message to current actor's stash ${msg}")
      // Adds the current message (the message that the actor received last) to the actor's stash.
      stash()
      stay().using(UserStorageFSM.EmptyData)
    case Event(_, _) =>
      println(s"--->[ERROR] unrecognized message / operation")
      // stay: No transition event will be triggered
      stay().using(UserStorageFSM.EmptyData)
  }

  /**
    * 2.2 define state - Connected state
    */
  when(UserStorageFSM.Connected) {
    case Event(UserStorageFSM.DisConnect, _) =>
      println(s"User storage disconnected to DB")
      goto(UserStorageFSM.Disconnected).using(UserStorageFSM.EmptyData)
    case Event(UserStorageFSM.Operation(op, user), _) =>
      println(s"User storage receive ${op} to do in user: ${user}")
      stay().using(UserStorageFSM.EmptyData)
    case Event(_,_) =>
      println(s"--->[ERROR] unrecognized message / operation")
      stay().using(UserStorageFSM.EmptyData)
  }

  /**
    * 3. Initialize state
    */
  initialize()
}


/**
  * Message object for UserStorage actor
  * @param userName
  * @param email
  */
case class User(userName: String, email: String)

/**
  * User storage operation object CRUD using FSM
  */
object UserStorageFSM {
  // FSM state
  sealed trait State
  case object Connected extends State
  case object Disconnected extends State

  // FSM Data
  sealed trait Data
  case object EmptyData extends Data

  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Delete extends DBOperation
  }

  case object Connect
  case object DisConnect
  case class Operation(dBOperation: DBOperation, user: Option[User])
}
