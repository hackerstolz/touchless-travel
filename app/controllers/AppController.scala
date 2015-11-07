package controllers

import models._
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc._

/**
  * Created by michi on 07/11/15.
  */
class AppController extends Controller with EventTrigger {

  implicit val geoJsonWrites = Json.writes[Geo]
  implicit val startStopStampJsonWrites = Json.writes[StartStopStamp]
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
        Ok(Json.obj("status" ->"OK", "message" -> ("Event "+event.toString+" saved.") ))
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
        EnterLeaveEvents.enterLeaveEvents += event
        Ok(Json.obj("status" ->"OK", "message" -> ("Event "+event.toString+" saved.") ))
      }
    )
  }

  def rides(userId: String) = Action {

    val userRides = UserRides.getForUser(userId)
    Ok(Json.toJson(userRides))
  }

}
