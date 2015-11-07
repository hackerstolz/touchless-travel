package controllers

import actors.MainActors._
import actors.{ActorFor, Connect}
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.http.HeaderNames
import play.api.libs.EventSource
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by Norman on 06.11.15.
  */
class Events extends Controller {

  implicit val timeout = Timeout(5 seconds) // needed for `?` below

  def connect(user: String) = Action.async {
    (userActor(user) ? Connect).mapTo[Enumerator[JsValue]].map(startEventStream)
  }

  private def userActor(user: String): ActorRef =
    Await.result((userActorManager ? ActorFor(user)).mapTo[ActorRef], 2 seconds)

  private def startEventStream(e: Enumerator[JsValue]): Result =
    Ok.feed(e &> EventSource()).as("text/event-stream")
      //.withHeaders(
    //HeaderNames.CACHE_CONTROL -> "no-cache")
    //HeaderNames.CONNECTION -> "keep-alive")

  //      withHeaders(
  //      HeaderNames.CONTENT_TYPE -> MimeTypes.EVENT_STREAM,
  //      HeaderNames.CACHE_CONTROL -> "no-cache",
  //      HeaderNames.CONNECTION -> "keep-alive")

}


