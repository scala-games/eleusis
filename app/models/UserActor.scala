package models

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import play.Logger
import rapture._
import core._, json._
import jsonBackends.jawn._

class UserActor(chatRoom: ActorRef, out: ActorRef, username: String) extends Actor {
  val logger = Logger.of(s"application.UserActor.$username")
  chatRoom ! Join(username)

  def receive = {
    case msg: CannotConnect =>
      logger.error(s"cannot connect:$msg")
      out ! Json(msg).toString

    case msg: Message =>
      val str = rapture.json.Json(msg).toString
      logger.info(s"sending message : $str")
      out ! str

    case s: String =>
      logger.info(s"user said: $s")
      val text = Json.parse(s).text.as[String]
      chatRoom ! Talk(username, text)
  }

  override def postStop(): Unit = {
    chatRoom ! Quit(username)
    logger.info("left")
  }
}
