package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.json._
import play.api.libs.iteratee._
import models._
import akka.actor._
import play.api.mvc._
import play.api.libs.streams._
import javax.inject.Inject
import akka.stream.Materializer

class Application @Inject() (implicit system: ActorSystem, materializer: Materializer) extends Controller {

  /**
   * Just display the home page.
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  /**
   * Display the chat room page.
   */
  def chatRoom(username: Option[String]) = Action { implicit request =>
    username.filterNot(_.isEmpty).map { username =>
      Ok(views.html.chatRoom(username))
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Please choose a valid username.")
    }
  }

  /**
   * Handles the chat websocket.
   */
  def chat(username: String) = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => ChatRoom.props(out, username))
  }

}