package controllers


import events.{TicketControlEvent, CheckoutEvent, CheckinEvent}
import play.api.mvc.{Action, Controller}

/**
  * Created by Norman on 07.11.15.
  */
class Test extends Controller with EventTrigger {


  def testCheckin = Action {

    val testCheckin = CheckinEvent(
      account = "Norman Weisenburger",
      transportationType = "TRAM",
      transportationName = "U9 direction Vogelsang",
      locationName = "Reitelsberg",
      lat = "34.87462875",
      lng = "34.47637882")

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
      lng = "34.47637882")

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
      lng = "34.47637882")

    raiseEvent(testTicketControl)
    Ok
  }

}
