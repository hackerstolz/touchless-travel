package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.Logger
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.{Json, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Norman on 06.11.15.
  */

// Life cycle events
case class Connect()

case class ActorFor(user: String)

case class Event(data: String)

class WebappUserActorManager extends Actor with ActorLogging {

  override def preStart() {
    log.info("User (connection) ActorManager started")
  }

  def receive = {
    case ActorFor(user) =>
      sender ! context.children.find(_.path.name == user).
        getOrElse(actorFor(user))
  }

  def actorFor(user: String): ActorRef = {
    val props = Props(new WebappUserActor(user))
    val connectedUser = context.actorOf(props, name = user)
    context.system.eventStream.subscribe(connectedUser, classOf[Event])
    Logger.info("Created UserActor for user: " + user)
    connectedUser
  }
}

class WebappUserActor(val user: String) extends Actor with ActorLogging {
  var notificationChannel: Option[Channel[JsValue]] = None

  def receive = {
    case Connect => connect
    case e: Event => {
      Logger.info("called notifiy")
      getChannelOrStop { c =>
        c.push(Json.toJson(e.data))
        Logger.info("Delivered message (" + user + "):" + e.data)
      }
    }
    case unknwon => log.info(
      "Received unprocessable message: " + unknwon.getClass.getName)
  }

  def connect = {
    val enumerator = Concurrent.unicast[JsValue](
      onStart = (c) => {
        Logger.info("Connected event source")
        notificationChannel = Some(c)
      },
      onComplete = stop,
      onError = (str, in) => {
        log.info(str)
        stop
      }).onDoneEnumerating(() => stop)
    sender ! enumerator
  }

  def stop: Unit = {
    log.info("Stopp method called")
    getChannelOrStop(c => c.eofAndEnd)
    context.stop(self)
  }

  private def getChannelOrStop(f: (Channel[JsValue]) => Unit) =
    if (notificationChannel.isDefined) f(notificationChannel.get)
    else stop

}