package models

import com.google.gson.Gson
import org.joda.time.DateTime
import play.api.libs.json.Json
import sorm.Persisted

import scala.util.parsing.json.JSONArray

/**
 * Created by dzhuraev on 3/16/16.
 */

case class PaperNew(title: String, description: String, creation_date: DateTime)

case class Page2[+A](items: Seq[A with Persisted], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object PaperNew {
//  implicit val newsFormat = Json.format[PaperNew]
  implicit val newsFormat = Json.format[PaperNew]

  def list2(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page2[PaperNew] = {

    val offest = pageSize * page

    val news = Db.query[PaperNew].limit(pageSize).offset(offest).fetch()

    val totalRows = Db.query[PaperNew].count()

    Page2(news, page, offest, totalRows)

  }

  def update(id: Long, paperNew: PaperNew) = {
    Db.query[PaperNew].whereEqual("id", id).replace(paperNew)
  }

  def findById(id: Long) = {
    Db.query[PaperNew].whereEqual("id", id).fetchOne()
  }

  def delete(id: Long) = {
    Db.delete[PaperNew](Db.fetchById[PaperNew](id))
  }

  def newsCount() = {
    Db.query[PaperNew].count()
  }

}