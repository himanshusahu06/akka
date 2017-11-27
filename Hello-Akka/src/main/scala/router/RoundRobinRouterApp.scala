package router

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.routing.RoundRobinGroup
import router.RandomGroupRouterApp.system
import router.actors.Worker
import router.RouterApp.router
import _root_.router.actors.Worker.Job

/**
  * Assign jobs to list of workers using round robin strategy
  */
object RoundRobinRouterApp extends App {

  val system = ActorSystem("round-robin-router")

  system.actorOf(Props[Worker], "w1")
  system.actorOf(Props[Worker], "w2")
  system.actorOf(Props[Worker], "w3")

  val workerList:List[String] = List(
    "/user/w1",
    "/user/w2",
    "/user/w3"
  )

  val roundRobinRouter = system.actorOf(RoundRobinGroup(workerList).props(), "round-robin-group")

  roundRobinRouter.!(Job(UUID.randomUUID(), "first job"))

  roundRobinRouter.!(Job(UUID.randomUUID(), "second job"))

  roundRobinRouter.!(Job(UUID.randomUUID(), "third job"))

  roundRobinRouter.!(Job(UUID.randomUUID(), "fourth job"))

  roundRobinRouter.!(Job(UUID.randomUUID(), "fifth job"))

  Thread.sleep(1000)

  system.terminate()
}
