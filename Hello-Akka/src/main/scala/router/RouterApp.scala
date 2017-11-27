package router

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import router.actors.Router
import router.actors.Worker.Job

object RouterApp extends App {

  val system = ActorSystem("Router")

  val router = system.actorOf(Props[Router])

  router.!(Job(UUID.randomUUID(), "first job"))

  router.!(Job(UUID.randomUUID(), "second job"))

  router.!(Job(UUID.randomUUID(), "third job"))

  router.!(Job(UUID.randomUUID(), "fourth job"))

  Thread.sleep(1000)

  system.terminate()
}
