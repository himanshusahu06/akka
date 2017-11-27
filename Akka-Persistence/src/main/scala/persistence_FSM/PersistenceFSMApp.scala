package persistence_FSM

import akka.actor.{ActorSystem, Props}
import persistence_FSM.Account.{Credit, Debit, Operation}

object PersistenceFSMApp extends App {

  val system = ActorSystem("persistence-fsm-app")

  val account = system.actorOf(Props[AccountActor], "account")

  account.!(Operation(1000, Credit))

  account.!(Operation(1, Debit))

  Thread.sleep(1000)

  system.terminate()
}
