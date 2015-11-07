package models

import java.sql.Timestamp

import org.joda.time.DateTime

/**
  * Created by michi on 07/11/15.
  */
case class UserRide(user: String, train: String, start: StartStopStamp, stopStamp: StartStopStamp)

case class StartStopStamp(timestamp: DateTime, geo: Geo)

case class Geo(lat: Long, long: Long)

object UserRides {

  var userRides = Seq[UserRide]()


  def getForUser(userId: String): Seq[UserRide] = {



    userRides.filter( r => r.user == userId)
  }

}
