package controllers

import actors.Event
import play.api.mvc._

class Application extends Controller with EventTrigger {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def testEvent = Action {
    raiseEvent(Event("blah"))
    Ok
  }

}
