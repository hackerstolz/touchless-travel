package actors

import akka.actor.Props
import play.api.Play.current
import play.api.libs.concurrent.Akka


/**
  * Created by Norman on 07.11.15.
  */
object MainActors {
  val userActorManager = Akka.system.actorOf(Props(classOf[WebappUserActorManager]), name = "user_actor_manager")
}
