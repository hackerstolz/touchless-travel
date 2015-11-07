package controllers

import events.{CheckoutEvent, CheckinEvent}
import models._
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import services.{TransportationService, LocationService}

/**
  * Created by michi on 07/11/15.
  */
class AppController extends Controller with EventTrigger {

  implicit val geoJsonWrites = Json.writes[Geo]
  implicit val startStopStampJsonWrites = Json.writes[StartStopStamp]
  implicit val trainJsonWrites = Json.writes[Train]
  implicit val userJsonWrites = Json.writes[User]
  implicit val userRideJsonWrites = Json.writes[UserRide]


  //JSON Macro Inception
  implicit val jsonWrites = Json.writes[EnterLeaveEvent]
  implicit val jsonReads = Json.reads[EnterLeaveEvent]

  def enterRegion = Action(parse.json) { request =>
    val placeResult = request.body.validate[EnterLeaveEvent]

    placeResult.fold(
      errors => {
        BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))
      },
      event => {
        EnterLeaveEvents.enterLeaveEvents += event.copy(etype = Some("ENTER"))
        Logger.debug(EnterLeaveEvents.enterLeaveEvents.toString())

        val trainOpt = Trains.getTrainForBeacon(event.beaconId)
        val userOpt = Users.get(event.userId)
        val timestamp = parseIosTimestamp(event.timestamp)

        if (trainOpt.isDefined && userOpt.isDefined ) {
          val train = trainOpt.get
          val user = userOpt.get
          val (geo, stationOpt) = LocationService.getTrainLocation(train.id)
          val currentLine = TransportationService.getCurrentLineForTrain(train.id)
          val stationName = stationOpt.flatMap(s => Some(s.name))

          // create new User ride
          UserRides.startRide(user, train, timestamp, geo, stationName)

          // raise UI event
          val checkinEvent = CheckinEvent(
            account = user.name,
            transportationType = train.getTransportationType,
            transportationName = currentLine,
            locationName = stationName.getOrElse("unknown"),
            lat = geo.lat,
            lng = geo.lng,
            timestamp = timestamp )

          raiseEvent(checkinEvent)

          Ok(Json.obj("status" ->"OK", "message" -> ("Event "+event.toString+" saved.") ))
        } else {
          BadRequest(Json.obj("status" ->"KO", "message" -> "Unable to find user and/or train"))
        }
      }
    )
  }

  def leaveRegion = Action(parse.json) { request =>
    val placeResult = request.body.validate[EnterLeaveEvent]

    placeResult.fold(
      errors => {
        BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))
      },
      event => {
        Logger.debug(EnterLeaveEvents.enterLeaveEvents.toString())
        EnterLeaveEvents.enterLeaveEvents += event

        val trainOpt = Trains.getTrainForBeacon(event.beaconId)
        val userOpt = Users.get(event.userId)
        val timestamp = parseIosTimestamp(event.timestamp)

        if (trainOpt.isDefined && userOpt.isDefined ) {
          val train = trainOpt.get
          val user = userOpt.get
          val (geo, stationOpt) = LocationService.getTrainLocation(train.id)
          val currentLine = TransportationService.getCurrentLineForTrain(train.id)
          val stationName = stationOpt.flatMap(s => Some(s.name))

          try {

            // create new User ride
            UserRides.stopRide(user, train, timestamp, geo, stationName)

            // raise UI event
            val checkoutEvent = CheckoutEvent(
              account = user.name,
              transportationType = train.getTransportationType,
              transportationName = currentLine,
              locationName = stationName.getOrElse("unknown"),
              lat = geo.lat,
              lng = geo.lng,
              timestamp = timestamp )

            raiseEvent(checkoutEvent)

            Ok(Json.obj("status" -> "OK", "message" -> ("Event " + event.toString + " saved.")))
          } catch {
            case NoMatchingPendingRide(msg) =>
              BadRequest(Json.obj("status" -> "KO", "message" -> msg))
          }
        } else {
          BadRequest(Json.obj("status" ->"KO", "message" -> "Unable to find user and/or train"))
        }
      })
  }

  def rides(userId: String) = Action {

    val userRides = UserRides.getForUser(userId)
    Ok(Json.toJson(userRides))
  }

  private def parseIosTimestamp(iosTimestamp: String): DateTime = {
    val splitArr = iosTimestamp.split("\\.")
    val unixTimestamp = splitArr(0).toLong
    new DateTime(unixTimestamp)
  }

}
