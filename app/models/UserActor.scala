package models

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import play.Logger
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json

class UserActor(chatRoom: ActorRef, out: ActorRef, username: String) extends Actor {
  val logger = Logger.of(s"application.UserActor.$username")
  chatRoom ! Join(username)

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
      chatRoom ! Talk(username, text.as[String])
  }

  override def postStop(): Unit = {
    chatRoom ! Quit(username)
    logger.info("left")
  }
}
