package models

import scala.concurrent.duration.DurationInt
import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.util.Timeout
import play.Logger
import play.api.libs.concurrent.Akka
import eleusis.game.Card
import eleusis.game.CardString

class ChatRoom extends Actor {
  var members = Map.empty[String, ActorRef]
  val robot = context.system.actorOf(Props(new GameRobot(self)))

  val logger = Logger.of("application.ChatRoom")

  def receive = {

    case Join(username) =>
      logger.info(s"$username wants to join")
      if (members.contains(username)) {
        sender ! CannotConnect(s"$username is already used")
      } else {
        members += username -> sender
        self ! NotifyJoin(username)
      }

    case NotifyJoin(username) =>
      notifyAll("join", username, "has entered the room")

    case Talk(username, text) =>
      notifyAll("talk", username, text)
      handleRobotCommands(username, text)

    case msg: HandMessage =>
      members(msg.user) ! msg

    case Quit(username) =>
      members -= username
      notifyAll("quit", username, "has left the room")

  }

  val playRe = "play (.*)".r

  private def handleRobotCommands(username: String, text: String): Unit = text match {
    case "start" =>
      robot ! Start(members.keys.toList)
      robot ! Status(username)
    case "stop" =>
      robot ! Stop
    case "status" =>
      robot ! Status(username)
    case playRe(cards) =>
      robot ! Play(username, cards)
      robot ! Status
    case _ =>
  }

  def notifyAll(kind: String, user: String, text: String) {
    val msg = Message(kind, user, text, members.keySet)

    logger.info(s"broadcast $msg")

    for (m <- members.values) {
      m ! msg
    }
  }

}

case class Join(username: String)
case class Quit(username: String)
case class Talk(username: String, text: String)
case class NotifyJoin(username: String)

case class Message(kind: String, user: String, message: String, members: Set[String])
case class HandMessage(cards: List[CardString], user: String, kind: String = "hand")

case class Connected(username: String)
case class CannotConnect(error: String)
