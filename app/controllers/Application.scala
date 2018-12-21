package controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.main3("Ognev CO | Team portfolio"))
  }


}
