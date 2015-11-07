package controllers

import actors.Event
import play.api.libs.json.Json
import play.api.mvc._


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


class Application extends Controller with EventTrigger {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def testCheckin = Action {

    val testCheckin = CheckinEvent(
      account = "Norman Weisenburger",
      transportationType = "TRAM",
      transportationName = "U9 direction Vogelsang",
      locationName = "Reitelsberg",
      lat = "34.87462875",
      lng = "34.47637882",
      timestamp = "234567890987")

    raiseEvent(testCheckin)
    Ok
  }

  def testCheckout = Action {

    val testCheckout = CheckoutEvent(
      account = "Norman Weisenburger",
      transportationType = "TRAM",
      transportationName = "U9 direction Vogelsang",
      locationName = "Reitelsberg",
      lat = "34.87462875",
      lng = "34.47637882",
      timestamp = "234567890987")

    raiseEvent(testCheckout)
    Ok
  }

  def testTicketControl = Action {

    val testTicketControl = TicketControlEvent(
      account = "Norman Weisenburger",
      transportationType = "TRAM",
      transportationName = "U9 direction Vogelsang",
      locationName = "Reitelsberg",
      controlType = "PASSED",
      lat = "34.87462875",
      lng = "34.47637882",
      timestamp = "234567890987")

    raiseEvent(testTicketControl)
    Ok
  }

}
