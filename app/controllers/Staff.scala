package controllers


import java.util.SimpleTimeZone

import models._
import org.joda.time.{DateTimeZone, DateTime}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Staff extends Controller {


  val Home = Redirect(routes.Staff.list(0, 2, ""))

//  val stafferForm: Form[Staffer] = Form(
//    mapping(
//      "name" -> text,
//      "image" -> text,
//      "birth" -> jodaDate("yyyy-MM-dd"),
//      "surname" -> text,
//      "middle_name" -> text,
//      "code" -> nonEmptyText,
//      "position" -> text,
//      "email" -> email
//    )(Staffer.apply)(Staffer.unapply))

//  def addStaff = Action { implicit request =>
//    stafferForm.bindFromRequest().fold(
//      formWithErrors => BadRequest(views.html.createForm(formWithErrors)),
//      staff => {
//        Db.save[Staffer](staff)
//        Home.flashing("success" -> "User %s has been created".format(staff.name))
//      }
//    )
//  }

  def getStaff = Action {
    val staffs = Db.query[Staffer].order("id", true).fetch()
//    staffs.
    Ok(Json.toJson(staffs))
  }

  def getOneStaff(id: Long) = Action {
    Ok(Json.toJson(Staffer.findById(id)))
  }

  def getStaffByQrCode(code: String) = Action {
    Ok(Json.toJson(Staffer.findByQrCode(code)))
  }

//  def create = Action {
//    Ok(views.html.createForm(stafferForm))
//  }

  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(views.html.staff(
      Staffer.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

//  def update(id: Long) = Action { implicit request =>
//    stafferForm.bindFromRequest.fold(
//    formWithErrors => BadRequest(views.html.editForm(id, formWithErrors)),
//    staff => {
//      Staffer.update(id, staff)
//      Home.flashing("success" -> "User %s has been updated".format(staff.name))
//    }
//    )
//  }

//  def edit(id: Long) = Action { implicit request =>
//    Staffer.findById(id).map { staff =>
//      Ok(views.html.editForm(id, stafferForm.fill(staff)))
//    }.getOrElse(NotFound)
//  }

  def delete(id: Long) = Action {
    Staffer.delete(id)
    Home.flashing("success" -> "User has been deleted")
  }

  def addHistory(code: String) = Action {
    val server = new GcmRestServer("AIzaSyBZC_-8MCJu6tM7H1P_RI7dfvXD7pMaGaY")
    val firstNames = Device.getAllDevices().map(_.token).toList

    val actionDateTime = DateTime.now() // TODO check

    Staffer.findByQrCode(code).map {
      staffer => {
        val counter: Int = History.historyCount(staffer, actionDateTime.getMillis)
        counter match {
          case 0 => {
            val history = History(staffer, 0, actionDateTime, actionDateTime.toLocalDate)
            Db.save[History](history)

            server.send(firstNames, Map(
              "message" ->" пришел",
              "name" -> staffer.name,
              "surname" -> staffer.surname
            ))

            Ok(Json.obj("status" -> "success", "count" -> counter, "staff" -> Json.toJson(staffer)))
          }
          case 1 => {
            val history = History(staffer, 1, actionDateTime, actionDateTime.toLocalDate)
            Db.save[History](history)

            server.send(firstNames, Map(
              "message" ->" ушел",
              "name" -> staffer.name,
              "surname" -> staffer.surname
            ))

            Ok(Json.obj("status" -> "success", "count" -> counter, "staff" -> Json.toJson(staffer)))
          }
          case 2 => {
            Ok(Json.obj("status" -> "fail", "count" -> counter,"staff" -> Json.toJson(staffer)))
          }
        }
      }
    }.getOrElse {
      Ok(Json.obj("status" -> "fail", "count" -> -1))
    }
  }


  def getHistory(code: String) = Action {
    Staffer.findByQrCode(code).map {
      history => {
        Ok(Json.toJson(History.findById(history)))
      }
    }.getOrElse {
      BadRequest(Json.obj("status" -> "fail", "message" -> "not found"))
    }
  }

  def getActionsByDate(userId: String, date: Long) = Action {

    Staffer.findByQrCode(userId).map {
      staff => {
        Ok(Json.toJson(History.getStaffActionsByDate(staff, date)))
      }
    }.getOrElse {
      BadRequest(Json.obj("status" -> "fail", "message" -> "not found"))
    }

  }

  def checkCode() = Action {
    var isExist = true
    var totalResult = ""

    do {
      val generatedCode = BearerTokenGenerator.generateMD5Token()

      Staffer.findByQrCode(generatedCode).map {
        staff => {
          isExist = true
        }
      }.getOrElse {
        isExist = false
        totalResult = generatedCode
      }
    } while(isExist)

    Ok(totalResult)
  }


  def deleteOneStaff(id: String) = Action {
    Staffer.findByQrCode(id).map {
      staff => {
        Staffer.deleteByCode(staff)
        Ok(Json.obj("status" -> "success", "message" -> "Сотрудник удален"))
      }
    }.getOrElse {
      BadRequest(Json.obj("status" -> "fail", "message" -> "Сотрудник не найден"))
    }
  }

  // handling POST request  from client side
  def addNewStaff() = Action { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    jsonBody.map { json => {
      val name = (json \ "name").as[String]
      val image = (json \ "image").as[String]
      val birth = (json \ "birth").as[Long]
      val surname = (json \ "surname").as[String]
      val middle_name = (json \ "middle_name").as[String]
      val code = (json \ "code").as[String]
      val position = (json \ "positions" \ "title").as[String]
      val email = (json \ "email").as[String]

      val staff = new Staffer(name, image, new DateTime(birth), surname, middle_name, code,
        Positions.findByTitle(position).get, email)
      Db.save[Staffer](staff)

      Ok(Json.obj("status" -> "success", "message" -> "Сотрудник добавлен"))
    }
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
    }

  def editStaff() = Action { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    jsonBody.map { json => {
      val name = (json \ "name").as[String]
      val image = (json \ "image").as[String]
      val birth = (json \ "birth").as[Long]
      val surname = (json \ "surname").as[String]
      val middle_name = (json \ "middle_name").as[String]
      val code = (json \ "code").as[String]
      val position = (json \ "positions" \ "title").as[String]
      val email = (json \ "email").as[String]

      Logger.debug("positions: " + position)


      Staffer.findByQrCode(code).map {
        staffer => {
          Staffer.updateStaffer(staffer, name, image, new DateTime(birth), surname, middle_name,
            Positions.findByTitle(position).get, email)
          Ok(Json.obj("status" -> "success", "message" -> "Сотрудник изменен"))
        }
      }.getOrElse{
        Ok(Json.obj("status" -> "fail", "message" -> "Сотрудник не найден"))
      }
    }
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  def getAllHistory(login: String, password: String) = Action {

    Admin.findAdmin(login, password).map {
      admin => {
        val histories = Db.query[History].order("id", true).fetch()
        Ok(Json.toJson(histories))
      }
    }.getOrElse {
      BadRequest(Json.obj("status" -> "fail", "message" -> "Администратор не найден"))
    }

  }
}
