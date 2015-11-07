package models


import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

case class NoMatchingPendingRide(msg: String) extends Exception(msg)

/**
  * Created by michi on 07/11/15.
  */
case class UserRide(user: User, vehicle: Vehicle,
                    distance: String = "", price: Double = 0.0,
                    start: StartStopStamp, stop: Option[StartStopStamp])

case class StartStopStamp(timestamp: DateTime, geo: Geo, stationName: Option[String])

case class Geo(lat: String, lng: String)

object UserRides {

  var userRides = ArrayBuffer.empty[UserRide]

  def add(ride: UserRide) = {
    userRides.append(ride)
  }

  def startTrainRide(user: User, train: Vehicle, timestamp: DateTime, geo: Geo, stationName: Option[String]) = {
    val start = StartStopStamp(timestamp, geo, stationName)
    val ride = UserRide(user, train, "" , 0.0, start, None)
    userRides.append(ride)
  }

  def stopTrainRide(user: User, train:Vehicle, timestamp: DateTime, geo: Geo, stationName: Option[String]) = {
     val pendingRideOpt = userRides.find { ride =>
       ride.user.id == user.id &&
       train.id == train.id &&
       ride.stop == None
     }
     if(pendingRideOpt.isDefined) {
       val pendingRide = pendingRideOpt.get
       val index = userRides.indexOf(pendingRide)
       val stop = StartStopStamp(timestamp, geo, stationName)
       val newPendingRide = pendingRide.copy(
         stop = Some(stop),
         distance = "2 Zones",
         price = 2.40)
       userRides.update(index, newPendingRide)
     } else {
       throw NoMatchingPendingRide("No matching pending ride found")
     }
  }

  def getForUser(userId: String): Seq[UserRide] = {
    userRides.filter( r => r.user.id == userId)
  }

}
