package models


import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

case class NoMatchingPendingRide(msg: String) extends Exception(msg)

/**
  * Created by michi on 07/11/15.
  */
case class UserRide(user: User, train: Train, start: StartStopStamp, stop: Option[StartStopStamp])

case class StartStopStamp(timestamp: DateTime, geo: Geo, stationName: Option[String])

case class Geo(lat: String, lng: String)

object UserRides {

  var userRides = ArrayBuffer.empty[UserRide]

  def startRide(user: User, train: Train, timestamp: DateTime, geo: Geo, stationName: Option[String]) = {
    val start = StartStopStamp(timestamp, geo, stationName)
    val ride = UserRide(user, train, start, None)
    userRides.append(ride)
  }

  def stopRide(user: User, train:Train, timestamp: DateTime, geo: Geo, stationName: Option[String]) = {
     val pendingRideOpt = userRides.find { ride =>
       ride.user.id == user.id &&
       train.id == train.id &&
       ride.stop == None
     }
     if(pendingRideOpt.isDefined) {
       val pendingRide = pendingRideOpt.get
       val index = userRides.indexOf(pendingRide)
       val stop = StartStopStamp(timestamp, geo, stationName)
       val newPendingRide = pendingRide.copy(stop = Some(stop))
       userRides.update(index, newPendingRide)
     } else {
       throw NoMatchingPendingRide("No matching pending ride found")
     }
  }

  def getForUser(userId: String): Seq[UserRide] = {
    userRides.filter( r => r.user.id == userId)
  }

}
