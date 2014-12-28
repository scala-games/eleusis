package models

import scala.concurrent.duration.DurationInt
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.util.Timeout
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import scala.concurrent.ExecutionContext.Implicits.global
import play.Logger
import play.api.libs.json.JsObject
import akka.actor.PoisonPill
import play.api.libs.json.Json

object ChatRoom {

  implicit val timeout = Timeout(1 second)

  def props(out: ActorRef, username: String) = Props(new UserActor(out, username))

  lazy val default = {
    val roomActor = Akka.system.actorOf(Props[ChatRoom])
    Akka.system.actorOf(Props(new GameRobot(roomActor)))
    roomActor
  }
}

class UserActor(out: ActorRef, username: String) extends Actor {
  val logger = Logger.of(s"application.UserActor.$username")
  ChatRoom.default ! Join(username)

  def receive = {
    case CannotConnect(msg) =>
      logger.error(s"cannot connect:$msg")
      out ! JsObject(Seq("error" -> JsString(msg)))

    case Message(msg) =>
      logger.info(s"sending message : $msg")
      out ! Json.stringify(msg)

    case s: String =>
      logger.info(s"user said: $s")
      val text = (Json.parse(s) \ "text")
      ChatRoom.default ! Talk(username, text.as[String])
  }

  override def postStop(): Unit = {
    ChatRoom.default ! Quit(username)
    logger.info("left")
  }
}

class ChatRoom extends Actor {
  var members = Map.empty[String, ActorRef]

  val logger = Logger.of("application.ChatRoom")

  def receive = {

    case Join(username) =>
      logger.info(s"$username wants to join")
      if (members.contains(username)) {
        sender ! CannotConnect(s"$username is already used")
      } else {
        members += username -> sender
        self ! NotifyJoin(username)
        //        sender ! Connected(username)
      }

    case NotifyJoin(username) =>
      notifyAll("join", username, "has entered the room")

    case Talk(username, text) =>
      notifyAll("talk", username, text)

    case Quit(username) =>
      members -= username
      notifyAll("quit", username, "has leaved the room")

  }

  def notifyAll(kind: String, user: String, text: String) {
    val msg = JsObject(
      Seq(
        "kind" -> JsString(kind),
        "user" -> JsString(user),
        "message" -> JsString(text),
        "members" -> JsArray(
          members.keySet.toList.map(JsString))))

    logger.info(s"broadcast $msg")

    members.foreach {
      case (_, a) => a ! Message(msg)
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
