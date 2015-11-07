package models

import scala.collection.mutable
/**
  * Created by michi on 07/11/15.
  */


/**
  *   {
    beaconId: "asdsad"
    userId: "asdas"
    timestamp: "234324234"
  }
  */
case class EnterLeaveEvent(beaconId: String, userId: String, timestamp: String)

object EnterLeaveEvents {
  val enterLeaveEvents = mutable.Buffer[EnterLeaveEvent]()
}