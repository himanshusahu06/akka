package playing_with_actors
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout
import playing_with_actors.Checker.{BlackListUser, CheckUser, WhiteListUser}
import playing_with_actors.Recorder.NewUser
import playing_with_actors.Storage.StoreUser
import akka.pattern.ask
import scala.concurrent.duration.DurationLong

/**
  * case class of user object
  * @param username
  * @param email
  */
case class User(username:String, email:String)

/**
  * Message object for Checker Actor
  */
object Checker {
  trait CheckerMessage
  // checker message
  case class CheckUser(user: User) extends CheckerMessage
  // checker response
  trait CheckerResponse
  case class BlackListUser(user: User) extends CheckerResponse
  case class WhiteListUser(user: User) extends CheckerResponse
}

/**
  * Message object for Storage Actor
  */
object Storage {
  trait StorageMessage
  case class StoreUser(user:User) extends StorageMessage
}

/**
  * Message object for Recorder Actor
  */
object Recorder {
  trait RecorderMessage
  case class NewUser(user:User) extends RecorderMessage
}

/**
  * this actor stores the user
  */
class Storage extends Actor {
  var users:List[User] = List.empty[User]

  override def receive = {
    case StoreUser(user:User) =>
      print(s"Storage: $user stored" )
      users ::= user
  }
}

/**
  * this actor check if a user is blacklisted or not
  */
class Checker extends Actor {

  val blacklistUser = List(
    User("hsahu", "hsahu@walmartlabs.com")
  )

  override def receive = {
    case  CheckUser(user: User) if blacklistUser.contains(user)=>
      println(s"user is blackListed: $user")
      sender().!(BlackListUser(user))
    case CheckUser(user: User) =>
      println(s"user is whiteListed: $user")
      sender().!(WhiteListUser(user))
    case _ =>
      println("unknown message")
  }
}

/**
  * this actor will manage the checker and storage actor
  * @param checker
  * @param storage
  */
class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5 seconds)

  override def receive = {
    case NewUser(user: User) =>
      checker.?(CheckUser(user))  map {
        case WhiteListUser(user) =>
          storage.!(StoreUser(user))
        case BlackListUser(user) =>
          println(s"blacklisted user: $user")
      }
  }
}

object TalkToActor extends App {

  val system = ActorSystem("TalkToActor")

  val checker = system.actorOf(Props[Checker], "checker")

  val storage = system.actorOf(Props[Storage], "storage")

  val recorder = system.actorOf(Props(new Recorder(checker, storage)), "recorder")

  recorder.!(NewUser(User("hsahu", "hsahu@walmartlabs.com")))

  Thread.sleep(100)

  system.terminate()
}
