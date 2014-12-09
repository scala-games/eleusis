package models

import akka.actor._
import scala.concurrent.duration.DurationInt
import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import scala.concurrent.Promise
import scala.collection.mutable.ListBuffer

object ChatRoom {

  implicit val timeout = Timeout(1 second)

  lazy val default = {
    val roomActor = Akka.system.actorOf(Props[ChatRoom])

    // Create a bot user (just for fun)
    Robot(roomActor)

    roomActor
  }

  //  def join(username: String) = {
  //    (default ? Join(username)).map {
  //
  //      case Connected(enumerator) =>
  //
  //        // Create an Iteratee to consume the feed
  //        val iteratee = Iteratee.foreach[JsValue] { event =>
  //          default ! Talk(username, (event \ "text").as[String])
  //        }.mapDone { _ =>
  //          default ! Quit(username)
  //        }
  //
  //        (iteratee, enumerator)
  //
  //      case CannotConnect(error) =>
  //
  //        // Connection error
  //
  //        // A finished Iteratee sending EOF
  //        val iteratee = Done[JsValue, Unit]((), Input.EOF)
  //
  //        // Send an error and close the socket
  //        val enumerator = Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))
  //
  //        (iteratee, enumerator)
  //
  //    }
  //
  //  }

}

class ChatRoom(out: ActorRef) extends Actor {
  var members = Set.empty[String]

  def receive = {

    case Join(username) => {
      if (members.contains(username)) {
        out ! CannotConnect(s"$username is already used")
      } else {
        members = members + username

        out ! Connected(username)
      }
    }

    case NotifyJoin(username) => {
      notifyAll("join", username, "has entered the room")
    }

    case Talk(username, text) => {
      notifyAll("talk", username, text)
    }

    case Quit(username) => {
      members = members - username
      notifyAll("quit", username, "has leaved the room")
    }

  }

  def notifyAll(kind: String, user: String, text: String) {
    //    val msg = JsObject(
    //      Seq(
    //        "kind" -> JsString(kind),
    //        "user" -> JsString(user),
    //        "message" -> JsString(text),
    //        "members" -> JsArray(
    //          members.keySet.toList.map(JsString))))
    //    members.foreach {
    //      case (_, channel) => channel.push(msg)
    //    }
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