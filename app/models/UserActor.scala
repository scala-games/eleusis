package models

import akka.actor.{Actor, ActorRef, actorRef2Scala}
import play.Logger
import play.api.libs.json.{JsObject, JsString, Json}

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
