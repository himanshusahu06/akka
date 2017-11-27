package router

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.routing.RandomGroup
import router.actors.Worker
import router.actors.Worker.Job

/**
  * random group router with inbuilt random-router-group actor
  */
object RandomGroupRouterApp extends App {

  val system = ActorSystem("Random-Group-Router")

  system.actorOf(Props[Worker], "w1")
  system.actorOf(Props[Worker], "w2")
  system.actorOf(Props[Worker], "w3")

  val workerList:List[String] = List(
    "/user/w1",
    "/user/w2",
    "/user/w3"
  )

  /**
    * create a list of paths for the worker and system will create a random-router-group to execute
    */
  val routerGroup = system.actorOf(RandomGroup(workerList).props(), "random-router-group")

  routerGroup.!(Job(UUID.randomUUID(), "first Job"))

  routerGroup.!(Job(UUID.randomUUID(), "second Job"))

  routerGroup.!(Job(UUID.randomUUID(), "third Job"))

  Thread.sleep(1000)

  system.terminate()
}
