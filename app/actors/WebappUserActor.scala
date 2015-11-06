package actors

import

/**
  * Created by Norman on 06.11.15.
  */

// Life cycle events
case class Connect()
case class ActorFor(user: String)

class WebappUserActorManager extends Actor {

  override def preStart() {
    log.info("User (connection) ActorManager started")
  }

  def receive = {
    case ActorFor(user) =>
      sender ! context.children.find(_.path.name == idAsString(user)).
        getOrElse(actorFor(user))
  }

  def actorFor(user: models.User): ActorRef = {
    val props = Props(new UserActor(user.id.get))
    val connectedUser = context.actorOf(props, name = idAsString(user))
    log.info("Created UserActor for userID: " + idAsString(user))
    connectedUser
  }
}

class WebappUserActor(userId: Long) extends Actor with ActorLogging {
  var notificationChannel: Option[Channel[JsValue]] = None

  def receive = {
    case Connect => connect
    case n: Notification => {
      log.info("called notifiy")
      getChannelOrStop { c =>
        c.push(Json.toJson(n))
        log.info("Delivered message: " + n.id)
      }
    }
    case unknwon => log.info(
      "Received unprocessable message: " + unknwon.getClass.getName)
  }

  def connect = {
    val enumerator = Concurrent.unicast[JsValue](
      onStart = (c) => {
        log.info("Connected event source")
        notificationChannel = Some(c)
      },
      onComplete = stop,
      onError = (str, in) => {
        Logger.info(str)
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