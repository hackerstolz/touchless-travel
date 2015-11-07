package events


import play.api.libs.json.Json
import play.api.libs.json.JsValue

/**
  * Created by Norman on 07.11.15.
  */

// Event
abstract class Event() {
  def toJSON: JsValue
}

case class CheckinEvent(
                         etype: String = "CHECKIN",
                         account: String,
                         transportationType: String,
                         transportationName: String,
                         locationName: String,
                         lat: String,
                         lng: String,
                         timestamp: String
                       ) extends Event {

  implicit val jsonWrites = Json.writes[CheckinEvent]

  def toJSON = Json.toJson(this)

}

case class CheckoutEvent(
                          etype: String = "CHECKOUT",
                          account: String,
                          transportationType: String,
                          transportationName: String,
                          locationName: String,
                          lat: String,
                          lng: String,
                          timestamp: String
                        ) extends Event {

  implicit val jsonWrites = Json.writes[CheckoutEvent]

  def toJSON = Json.toJson(this)

}

case class TicketControlEvent(
                               etype: String = "CONTROL",
                               account: String,
                               transportationType: String,
                               transportationName: String,
                               locationName: String,
                               controlType: String,
                               lat: String,
                               lng: String,
                               timestamp: String
                             ) extends Event {

  implicit val jsonWrites = Json.writes[TicketControlEvent]

  def toJSON = Json.toJson(this)

}
