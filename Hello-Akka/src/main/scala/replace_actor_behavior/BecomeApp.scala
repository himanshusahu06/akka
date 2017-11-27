package replace_actor_behavior

import akka.actor.{ActorRef, ActorSystem, Props}
import replace_actor_behavior.router.{User, UserStorage}

object BecomeApp extends App {

  import UserStorage._

  val system = ActorSystem("become-app")

  val userStorage:ActorRef = system.actorOf(Props[UserStorage], "userstorage")

  userStorage.!(Connect)

  userStorage.!(Operation(DBOperation.Create, Some(User("hsahu","hsahu@walmartlabs.com"))))

  userStorage.!(DisConnect)

  userStorage.!(Operation(DBOperation.Create, Some(User("himanshu sahu","himanshusahu24@gmail.com"))))

  userStorage.!(Operation(DBOperation.Create, Some(User("Admin","foo@bar.com"))))

  userStorage.!(Connect)

  Thread.sleep(1000)

  system.terminate()
}
