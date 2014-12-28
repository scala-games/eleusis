package models

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

import akka.actor.{ Actor, ActorRef }
import akka.actor.actorRef2Scala
import play.api.Play.current
import play.api.libs.concurrent.Akka

class GameRobot(chatRoom: ActorRef) extends Actor {

  // Make the robot talk every 30 seconds
  Akka.system.scheduler.schedule(
    30 seconds,
    30 seconds,
    chatRoom,
    Talk("Robot", "I'm still alive"))

  val receive: Receive = {
    case _ =>
  }
}