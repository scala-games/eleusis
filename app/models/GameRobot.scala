package models

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import eleusis.game.Cards
import eleusis.game.GameState
import eleusis.game.Player

class GameRobot(chatRoom: ActorRef) extends Actor {
  var gameState: Option[GameState] = None

  // Make the robot talk every 30 seconds
  context.system.scheduler.schedule(
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
      }
    case Stop =>
      gameState = None
    case Status(username) =>
      val msg = gameState match {
        case Some(state) =>
          val hand = state.players.collect {
            case p: Player if p.name == username => p.hand
          }.head
          HandMessage(hand.map(_.toCardString), username)
        case None => CannotConnect("game not started")
      }
      chatRoom ! msg

    case Play(username, cardsString) =>
      gameState match {
        case Some(s) =>
          val cards = Cards(cardsString).take(4) // TODO : check cards are actually in hand
          gameState = Some(s.play(username, cards: _*))
        case None =>
          chatRoom ! Talk("robot", "game is not started")
      }

  }
}

case class Start(players: List[String])

case object Stop
case class Status(username: String)

case class Play(username: String, cards: String)