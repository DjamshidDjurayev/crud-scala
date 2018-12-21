package controllers

import models.{Staffer, Db, PaperNew}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json


class News extends Controller {

  val Home = Redirect(routes.News.list2(0, 2, ""))

  val newsForm: Form[PaperNew] = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> nonEmptyText,
      "creation_date" -> jodaDate
    )(PaperNew.apply)(PaperNew.unapply)
  )

  def list2(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(views.html.news(
      PaperNew.list2(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  def addNews = Action { implicit request =>
    newsForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.createFormNews(formWithErrors)),
      newsPaper => {
        Db.save[PaperNew](newsPaper)
        Home.flashing("success" -> "News %s has been created".format(newsPaper.title))
      }
    )
  }

  def create = Action {
    Ok(views.html.createFormNews(newsForm))
  }

  def update(id: Long) = Action { implicit request =>
    newsForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.editFormNews(id, formWithErrors)),
      paperNew => {
        PaperNew.update(id, paperNew)
        Home.flashing("success" -> "User %s has been updated".format(paperNew.title))
      }
    )

  }

  def edit(id: Long) = Action { implicit request =>
    PaperNew.findById(id).map { paperNew =>
      Ok(views.html.editFormNews(id, newsForm.fill(paperNew)))
    }.getOrElse(NotFound)
  }

  def delete(id: Long) = Action {
    PaperNew.delete(id)
    Home.flashing("success" -> "News has been deleted")
  }

  def getNews = Action {
    val news = Db.query[PaperNew].order("id", true).fetch()
    Ok(Json.toJson(news))
  }

  def getOneNews(id: Long) = Action {
    Ok(Json.toJson(PaperNew.findById(id)))
  }

//  def addOneNews(title: String, description: String) = Action {
//    val newsData = Map("title" -> title, "description" -> description, "image" -> "")
//
//    newsForm.bind(newsData).fold(
//      isError => BadRequest(views.html.createFormNews(isError)),
//      isOkay => {
//        DBase.save[PaperNew](isOkay)
//        Ok("okay")
//      }
//    )
//  }


}
