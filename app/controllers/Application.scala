package controllers


import play.api.mvc._


class Application extends Controller with EventTrigger {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }



}
