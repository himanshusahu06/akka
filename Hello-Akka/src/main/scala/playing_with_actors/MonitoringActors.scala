package playing_with_actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}

/**
  * Parent Actor watching some of child actor
  * @param child
  * @param child2
  */
class Parent(child: ActorRef, child2: ActorRef) extends Actor {

  override def preStart(): Unit = {
    /**
      * monitor child
      */
    context.watch(child)
    context.watch(child2)
  }

  override def postStop(): Unit = {
    println(s"Parent stop")
  }

  /**
    * Parent will receive a termination message when it's child being watched are terminated by 3rd party
    * @return
    */
  override def receive = {
    case Terminated(actorRef: ActorRef) if actorRef.equals(child) =>
      println(s"Parent watched: <"+ child.path.name +"> actor termination.. unwatching actor")
      context.unwatch(child)
    case Terminated(actorRef: ActorRef) if actorRef.equals(child2) =>
      println(s"Parent watched: <"+ child2.path.name +"> actor termination.. unwatching actor")
      context.unwatch(child2)
    case _ =>
      println("something happened")
      context.stop(self)
  }
}

class Child extends Actor {

  override def receive = {
    case message =>
      println(s"Child received ${message}")
      context.stop(self)
  }
}

class Child2 extends Actor {

  override def receive = {
    case message =>
      println(s"Child received ${message}")
      context.stop(self)
  }
}

object MonitoringActors extends App {

  val system = ActorSystem("MonitoringActors")

  val child = system.actorOf(Props[Child], "child")

  val child2 = system.actorOf(Props[Child], "child2")

  val parent = system.actorOf(Props(classOf[Parent], child, child2), "parent")

  child2.!("Hi")

  Thread.sleep(500)

  //system.terminate()
}
