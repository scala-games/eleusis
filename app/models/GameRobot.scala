package models

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import akka.actor.{ Actor, ActorRef }
import akka.actor.actorRef2Scala
import play.api.Play.current
import play.api.libs.concurrent.Akka
import eleusis.game.Cards
import eleusis.game.GameState

class GameRobot(chatRoom: ActorRef) extends Actor {
  var gameState: Option[GameState] = None

  // Make the robot talk every 30 seconds
  Akka.system.scheduler.schedule(
    30 seconds,
    30 seconds,
    chatRoom,
    Talk("Robot", "I'm still alive"))

  val receive: Receive = {
    case Start(players) =>
      gameState match {
        case Some(s) => chatRoom ! Talk("robot", "game is already started")
        case None =>
          gameState = Some(GameState(players))
          chatRoom ! Talk("robot", gameState.toString)
      }
    case Stop => gameState = None
  }
}

case class Start(players: List[String])

case object Stop