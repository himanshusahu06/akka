package replace_actor_behavior.router

import akka.actor.{Actor, Stash}
import replace_actor_behavior.router.UserStorage.{Connect, DisConnect, Operation}

/**
  * Actor that changes it's behavior on runtime.
  *
  * The Stash enables an actor to temporarily stash away messages that can not or
  * should not be handled using the actor's current behavior.
  */
class UserStorage extends Actor with Stash {

  /**
    * The connected behavior of actor
    * @return
    */
  def connected: Actor.Receive = {
    case DisConnect =>
      println(s"User storage disconnected to DB")
      /**
        * Reverts the Actor behavior to the previous one on the behavior stack.
        */
      context.unbecome()
    case Operation(op, user) =>
      println(s"User storage receive ${op} to do in user: ${user}")
    case _ =>
      println(s"--->[ERROR] unrecognized message / operation")
  }

  /**
    * The disconnected behavior of actor
    * @return
    */
  def disconnected: Actor.Receive = {
    case Connect =>
      println(s"User storage connected to DB")
      /**
        * Prepends all messages in the stash to the mailbox, and then clears the stash
        */
      unstashAll()
      /**
        * Changes the Actor's behavior to become the new 'Receive'.
        * Replaces the current behavior on the top of the behavior stack.
        */
      context.become(connected)
    case msg:Operation =>
      println(s"--->[INFO] adding message to current actor's stash ${msg}")
      /**
        * Adds the current message (the message that the actor received last) to the
        * actor's stash.
        */
      stash()
    case _ =>
      println(s"--->[ERROR] unrecognized message / operation")
  }

  /**
    * actor should start with default disconnected behavior
    * @return
    */
  override def receive = disconnected
}


/**
  * Message object for UserStorage actor
  * @param userName
  * @param email
  */
case class User(userName: String, email: String)

/**
  * User storage operation object CRUD
  */
object UserStorage {

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
