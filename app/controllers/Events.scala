package controllers

import play.api._
import play.api.mvc._


/**
  * Created by Norman on 06.11.15.
  */
class Events extends Controller {

  def connect = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}


