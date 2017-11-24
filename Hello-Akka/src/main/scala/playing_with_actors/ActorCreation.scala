package playing_with_actors

import akka.actor.{Actor, ActorSystem, Props}
import playing_with_actors.MusicPlayer.{StartMusic, StopMusic}
import playing_with_actors.MusicController.{Play, Stop}

/**
  * music controller Message
*/
object MusicController {
  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object Stop extends ControllerMsg

  def prop = Props[MusicController]
}

/**
  * music player object
  */
object MusicPlayer {
  sealed trait PlayMsg
  case object StopMusic extends PlayMsg
  case object StartMusic extends PlayMsg
}


/**
  * Music controller Actor
  */
class MusicController extends Actor {
  override def receive = {
    case Play => {
      println("Music started....")
    }
    case Stop => {
      println("Music stopped....")
    }
  }
}

/**
  * Music player Actor
  */
class MusicPlayer extends Actor {
  override def receive = {
    case StopMusic => {
      println("I don't want to stop music..")
    }
    case StartMusic => {
      val musicController = context.actorOf(MusicController.prop, "musicController")
      musicController.!(MusicController.Play)
    }
    case _ => println("Unknown Message...")
  }
}

object ActorCreation extends App {

  // create actor system
  val system = ActorSystem("creation")

  // create Music Player actor
  val player = system.actorOf(Props[MusicPlayer], "musicPlayer")

  // start the music
  player.!(StartMusic)

  // stop the music
  //player.!(StopMusic)

  system.terminate()
}
