package controllers

import models.{EnterLeaveEvents, EnterLeaveEvent}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc._

/**
  * Created by michi on 07/11/15.
  */
class AppController extends Controller with EventTrigger {


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
        EnterLeaveEvents.enterLeaveEvents += event
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

}
