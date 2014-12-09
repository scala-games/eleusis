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

object ChatRoom {

  implicit val timeout = Timeout(1 second)

  def props(out: ActorRef, username: String) = Props(new UserActor(out, username))

  lazy val default = {
    val roomActor = Akka.system.actorOf(Props[ChatRoom])
    // Create a bot user (just for fun)
    Robot(roomActor)
    roomActor
  }
}

class UserActor(out: ActorRef, username: String) extends Actor {
  ChatRoom.default ! Join(username)

  def receive = {
    case CannotConnect(msg) =>
      out ! JsObject(Seq("error" -> JsString(msg)))

    case j: JsObject =>
      out ! j
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
      case (_, a) => a ! msg
    }
  }

}

case class Join(username: String)
case class Quit(username: String)
case class Talk(username: String, text: String)
case class NotifyJoin(username: String)

case class Connected(username: String)
case class CannotConnect(msg: String)

object Robot {

  def apply(chatRoom: ActorRef) {

    // Make the robot talk every 30 seconds
    Akka.system.scheduler.schedule(
      30 seconds,
      30 seconds,
      chatRoom,
      Talk("Robot", "I'm still alive"))
  }

}