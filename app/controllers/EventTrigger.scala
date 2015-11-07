package controllers

/**
  * Created by Norman on 07.11.15.
  */

import actors.Event
import play.api.libs.concurrent.Akka
import play.api.Play.current
//import scala.concurrent.ExecutionContext.Implicits.global


trait EventTrigger {

  val eventStream = Akka.system.eventStream

  def raiseEvent(e: Event) = eventStream.publish(e)

}
