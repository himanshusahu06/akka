package router

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import router.actors.Worker.Job
import router.actors.{RouterGroup, Worker}

/**
  * random group router with custom RouterGroup actor
  */
object RouterGroupApp extends App {

  val system = ActorSystem("Router")

  system.actorOf(Props[Worker], "w1")
  system.actorOf(Props[Worker], "w2")
  system.actorOf(Props[Worker], "w3")
  system.actorOf(Props[Worker], "w4")
  system.actorOf(Props[Worker], "w5")

  val workerList:List[String] = List(
    "/user/w1",
    "/user/w2",
    "/user/w3",
    "/user/w4",
    "/user/w5"
  )

  val routerGroup = system.actorOf(Props(classOf[RouterGroup], workerList))

  routerGroup.!(Job(UUID.randomUUID(), "first job"))

  routerGroup.!(Job(UUID.randomUUID(), "second job"))

  routerGroup.!(Job(UUID.randomUUID(), "third job"))

  routerGroup.!(Job(UUID.randomUUID(), "fourth job"))

  Thread.sleep(1000)

  system.terminate()
}
