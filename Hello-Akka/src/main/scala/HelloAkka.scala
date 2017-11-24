import akka.actor.{Actor, ActorSystem, Props}

/**
  * define actor message
  * @param who
  */
case class WhoToGreet(who:String)

/**
  * define actor
  */
class Greeter extends Actor {
  override def receive: Receive = {
    case WhoToGreet(who) => println(s"Hello $who")
    case default => println("You gave me some random message")
  }
}

object HelloAkka extends App {

  // create akka actor system
  val actorSystem = ActorSystem("hello-akka-actor-system")

  // create the actor
  val greeter = actorSystem.actorOf(Props[Greeter], "greeter")

  val message:WhoToGreet = new WhoToGreet("Himanshu")

  // send message to actor
  greeter !(message)

  // terminate actor system
  actorSystem.terminate()
}