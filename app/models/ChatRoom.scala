package models

import scala.concurrent.duration.DurationInt

import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.util.Timeout
import play.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.{ JsArray, JsObject, JsString }

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
    val msg = JsObject(
      Seq(
        "kind" -> JsString(kind),
        "user" -> JsString(user),
        "message" -> JsString(text),
        "members" -> JsArray(members.keySet.toList.map(JsString))))

    logger.info(s"broadcast $msg")

    for (m <- members.values) {
      m ! Message(msg)
    }
  }

 

}

case class Join(username: String)
case class Quit(username: String)
case class Talk(username: String, text: String)
case class NotifyJoin(username: String)

case class Message(msg: JsObject)

case class Connected(username: String)
case class CannotConnect(msg: String)
