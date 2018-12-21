package models

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes, Json}
import sorm.Persisted

/**
 * Created by dzhuraev on 4/28/16.
 */
case class Admin(name: String, surname: String, login: String, password: String)

object Admin {

  implicit val adminFormat = Json.format[Admin]

//  implicit val adminWrite = Json.writes[Admin]


//  implicit val adminWrite: Writes[Admin] = (
//      (JsPath \ "name").write[String] and
//      (JsPath \ "surname").write[String] and
//      (JsPath \ "login").write[String] and
//      (JsPath \ "password").write[String]
//    )(unlift(Admin.unapply))




//  implicit val adminWrite = Json.toJsFieldJsValueWrapper(
//  "id" -> 1,
//  "name" -> "name",
//  "surname" -> "surname",
//  "login" -> "login",
//  "password" -> "password"
//  )


  def findAdmin(login: String, password: String) = {
    Db.query[Admin with Persisted].whereEqual("login", login).whereEqual("password", password).fetchOne()
  }

  def findAdminByLogin(login: String) = {
    Db.query[Admin with Persisted].whereEqual("login", login).fetchOne()
  }

  def getAdminId(login: String, password: String) = {
    Db.query[Admin].whereEqual("login", login).whereEqual("password", password).fetchOneId()
  }

  def save(admin: Admin) = {
    Db.save[Admin](admin)
  }

  def delete(admin: Admin) = {
    Db.delete[Admin](admin)
  }

  def adminCount() = {
    Db.query[Admin].count()
  }

//  def isExist(login: String, password: String) = {
//    DBase.query[Admin].exists()
//  }

//  def getAdmins() = {
//    DBase.query[Admin].fetch()
//
////    DBase.query[Admin].fetch()
//
//  }
}