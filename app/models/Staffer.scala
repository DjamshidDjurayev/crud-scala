package models


import org.joda.time.{LocalDate, DateTime}
import play.api.libs.json.{Writes, Reads, JsPath, Json}
import sorm._
import play.api.libs.functional.syntax._

/**
 * Created by dzhuraev on 3/15/16.
 */
case class Staffer(name: String, image: String, birth: DateTime, surname: String, middle_name: String, code: String, positions: Positions, email: String)

case class Page[+A](items: Seq[A with Persisted], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Staffer {
  implicit val format = Json.format[Staffer]

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Staffer] = {

    val offest = pageSize * page

    val users = Db.query[Staffer].limit(pageSize).order("id", true).offset(offest).fetch()

    val totalRows = Db.query[Staffer].count()

    Page(users, page, offest, totalRows)

  }

  def update(id: Long, staffer: Staffer) = {
    Db.query[Staffer].whereEqual("id", id).replace(staffer)
  }

  def findById(id: Long) = {
    Db.query[Staffer].whereEqual("id", id).fetchOne()
  }

  def findByQrCode(code: String) = {
    Db.query[Staffer].whereEqual("code", code).fetchOne()
  }

  def delete(id: Long) = {
    Db.delete[Staffer](Db.fetchById[Staffer](id))
  }

  def deleteByCode(staffer: Staffer) = {
    Db.delete[Staffer](staffer)
  }

  def updateStaffer(staffer: Staffer, name: String, image: String,
                    birth: DateTime, surname: String, middle_name: String,
                    positions: Positions, email: String) = {
    Db.save(staffer.copy(name = name, image = image, birth = birth,
      surname = surname, middle_name = middle_name, positions = positions, email = email))
  }

  def staffCount() = {
    Db.query[Staffer].count()
  }


}

