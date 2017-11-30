package cluster

import akka.actor.{ActorRef, PoisonPill}
import common.Add

object ClusterApp extends App {

  // initialize single frontend node
  Frontend.initiate()

  //initialize three nodes for backend
  val nodeA:ActorRef = Backend.initiate(2552)
  val nodeB:ActorRef = Backend.initiate(2560)
  val nodeC:ActorRef = Backend.initiate(2561)

  Thread.sleep(5000)

  println("\n\n\n\n\n")

  Frontend.getFrontEnd.!(Add(2,3))

  Frontend.getFrontEnd.!(Add(1,1))

  Thread.sleep(3000)

  nodeA.!(PoisonPill)

}
