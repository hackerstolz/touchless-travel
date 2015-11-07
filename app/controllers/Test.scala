package controllers


import events.{TicketControlEvent, CheckoutEvent, CheckinEvent}
import models.DemoData
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
    Ok(testCheckin.toJSON)
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
    Ok(testCheckout.toJSON)
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
    Ok(testTicketControl.toJSON)
  }


  def populateDemoData = Action {
     if(DemoData.demoDataPopulated == false) {
       DemoData.populate
       DemoData.demoDataPopulated = true
       Ok("Successfully populated demo data")
     } else {
      Ok("Demo data already populated")
     }
  }

}
