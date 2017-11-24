package playing_with_actors

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import scala.concurrent.duration._

object ChildActorMessages {
  case object ResumeException extends Exception
  case object StopException extends Exception
  case object RestartException extends Exception
}

/**
  * child actor to be supervised
  */
class ChildActor extends Actor {

  import ChildActorMessages._

  override def preStart() = {
    println("ChildActor preStart...")
  }

  override def postStop() = {
    println("ChildActor postStop...")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    println("ChildActor preRestart...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    println("ChildActor postRestart...")
    super.postRestart(reason)
  }

  override def receive = {
    case "resume" =>
      throw ResumeException
    case "stop" =>
      throw StopException
    case "restart" =>
      throw RestartException
    case _ =>
      throw new Exception()
  }
}

/**
  * parent actor with 1-1 supervise strategy
  */
class ParentActor extends Actor {

  import ChildActorMessages._

  var childRef:ActorRef = _

  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
    case RestartException => Restart
    case ResumeException => Resume
    case StopException => Stop
    case _:Exception => Escalate
  }

  override def preStart() = {
    childRef = context.actorOf(Props[ChildActor], "child")
    Thread.sleep(100)
  }

  override def receive = {
    case msg:String =>
      println(s"Parent Received : $msg")
      childRef.!(msg)
  }
}

object SupervisionActors extends App {

  // Create the 'supervision' actor system
  val system:ActorSystem = ActorSystem("SupervisionActors")

  // Create parent Actor
  val parentActor = system.actorOf(Props[ParentActor], "parent-actor")

  // send message
  parentActor.!("stop")

  Thread.sleep(100)

  system.terminate()

}
