package controllers

import models.DemoData
import play.api.mvc.{Action, Controller}
import services.LocationService

/**
  * Created by Norman on 07.11.15.
  */
class Admin extends Controller {

  def populateDemoData = Action {
    if(DemoData.demoDataPopulated == false) {
      DemoData.populate
      DemoData.demoDataPopulated = true
      Ok("Successfully populated demo data")
    } else {
      Ok("Demo data already populated")
    }
  }

  def resetDemo = Action {
    LocationService.callCounter = 0
    Ok("Demo reset -> Ready to run!")
  }

}
