package router

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.routing.FromConfig
import router.actors.Worker
import router.actors.Worker.Job

/**
  * Using random-router-pool from configuration file. For this no need to create Router Actor
  */
object RandomRouterApp extends App {

  val system = ActorSystem("Random-Router-Pool")

  val routerPool = system.actorOf(FromConfig.props(Props[Worker]), "random-router-pool")

  routerPool.!(Job(UUID.randomUUID(), "first Job"))

  routerPool.!(Job(UUID.randomUUID(), "second Job"))

  routerPool.!(Job(UUID.randomUUID(), "third Job"))

  Thread.sleep(1000)

  system.terminate()
}
