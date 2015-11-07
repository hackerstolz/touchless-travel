package controllers

import events.{TicketControlEvent, CheckoutEvent, CheckinEvent}
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

  case class UserRidesResponse(summedPrice: Double, savedPrice: Double, co2saved: Int, rides: Seq[UserRide])


  //JSON Macro Inception
  implicit val geoJsonWrites = Json.writes[Geo]
  implicit val startStopStampJsonWrites = Json.writes[StartStopStamp]
  implicit val trainJsonWrites = Json.writes[Vehicle]
  implicit val userJsonWrites = Json.writes[User]
  implicit val userRideJsonWrites = Json.writes[UserRide]
  implicit val userRideResponseJsonWrites = Json.writes[UserRidesResponse]

  implicit val jsonWrites = Json.writes[EnterLeaveEvent]
  implicit val jsonReads = Json.reads[EnterLeaveEvent]

  def enterRegion = Action(parse.json) { request =>
    val placeResult = request.body.validate[EnterLeaveEvent]

    placeResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      event => {
        EnterLeaveEvents.enterLeaveEvents += event.copy(etype = Some("ENTER"))
        Logger.debug(EnterLeaveEvents.enterLeaveEvents.toString())

        val trainOpt = Vehicles.getTrainForBeacon(event.beaconId)
        val userOpt = Users.get(event.userId)
        val timestamp = parseIosTimestamp(event.timestamp)

        if (trainOpt.isDefined && userOpt.isDefined) {
          val train = trainOpt.get
          val user = userOpt.get
          val (geo, stationOpt) = LocationService.getTrainLocation(train.id)
          val currentLine = TransportationService.getCurrentLineForTrain(train.id)
          val stationName = stationOpt.flatMap(s => Some(s.name))

          // create new User ride
          UserRides.startTrainRide(user, train, timestamp, geo, stationName)

          // raise UI event
          val checkinEvent = CheckinEvent(
            account = user.name,
            transportationType = train.vtype,
            transportationName = currentLine,
            locationName = stationName.getOrElse("unknown"),
            lat = geo.lat,
            lng = geo.lng,
            timestamp = timestamp)

          raiseEvent(checkinEvent)

          Ok(Json.obj("status" -> "OK", "message" -> ("Event " + event.toString + " saved.")))
        } else {
          BadRequest(Json.obj("status" -> "KO", "message" -> "Unable to find user and/or train"))
        }
      }
    )
  }

  def leaveRegion = Action(parse.json) { request =>
    val placeResult = request.body.validate[EnterLeaveEvent]

    placeResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      event => {
        Logger.debug(EnterLeaveEvents.enterLeaveEvents.toString())
        EnterLeaveEvents.enterLeaveEvents += event

        val trainOpt = Vehicles.getTrainForBeacon(event.beaconId)
        val userOpt = Users.get(event.userId)
        val timestamp = parseIosTimestamp(event.timestamp)

        if (trainOpt.isDefined && userOpt.isDefined) {
          val train = trainOpt.get
          val user = userOpt.get
          val (geo, stationOpt) = LocationService.getTrainLocation(train.id)
          val currentLine = TransportationService.getCurrentLineForTrain(train.id)
          val stationName = stationOpt.flatMap(s => Some(s.name))

          try {

            // create new User ride
            UserRides.stopTrainRide(user, train, timestamp, geo, stationName)

            // raise UI event
            val checkoutEvent = CheckoutEvent(
              account = user.name,
              transportationType = train.vtype,
              transportationName = currentLine,
              locationName = stationName.getOrElse("unknown"),
              lat = geo.lat,
              lng = geo.lng,
              timestamp = timestamp)

            raiseEvent(checkoutEvent)

            Ok(Json.obj("status" -> "OK", "message" -> ("Event " + event.toString + " saved.")))
          } catch {
            case NoMatchingPendingRide(msg) =>
              BadRequest(Json.obj("status" -> "KO", "message" -> msg))
          }
        } else {
          BadRequest(Json.obj("status" -> "KO", "message" -> "Unable to find user and/or train"))
        }
      })
  }

  def ticketControl = Action(parse.anyContent) { request =>
    val userIdOpt = request.getQueryString("userId")
    val timestampOpt = request.getQueryString("timestamp")
    val beaconIdOpt = request.getQueryString("beaconId")
    if (userIdOpt.isDefined && beaconIdOpt.isDefined) {
      val userOpt = Users.get(userIdOpt.get)
      val trainOpt = Vehicles.getTrainForBeacon(beaconIdOpt.get)
      val timestamp = timestampOpt.flatMap(t => Some(parseIosTimestamp(t))).getOrElse(new DateTime)
      if (userOpt.isDefined && trainOpt.isDefined) {
        val train = trainOpt.get
        val user = userOpt.get
        val (geo, stationOpt) = LocationService.getTrainLocation(train.id)
        val currentLine = TransportationService.getCurrentLineForTrain(train.id)
        val stationName = stationOpt.flatMap(s => Some(s.name))

        // raise UI event
        val ticketControl = TicketControlEvent(
          account = user.name,
          transportationType = train.vtype,
          transportationName = currentLine,
          locationName = stationName.getOrElse("unknown"),
          controlType = "PASSED",
          lat = geo.lat,
          lng = geo.lng,
          timestamp = timestamp)

        raiseEvent(ticketControl)

        Ok(Json.obj("status" -> "OK", "message" -> ("Ticket Control Passed")))
      } else {
        BadRequest(Json.obj("status" -> "KO", "message" -> "Unable to find user and/or train"))
      }
    } else {
      BadRequest(Json.obj("status" -> "KO", "message" -> "Unable to parse required params"))
    }
  }

  def rides(userId: String) = Action {
    val userRides = UserRides.getForUser(userId).sortBy(_.start.timestamp.getMillis)

    val summedPrice = userRides.map(_.price).sum
    val savedPrice = summedPrice * 0.1
    val co2saved = 80

    val response = UserRidesResponse(summedPrice, savedPrice, co2saved, userRides)

    Ok(Json.toJson(response))
  }


  private def parseIosTimestamp(iosTimestamp: String): DateTime = {
    val splitArr = iosTimestamp.split("\\.")
    val unixTimestamp = splitArr(0).toLong
    new DateTime(unixTimestamp)
  }

}
