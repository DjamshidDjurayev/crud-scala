package models


import java.util.{Date}

import org.joda.time.{LocalDate, DateTime}
import play.api.libs.json.Json
import sorm.joda.Extensions.DateToJoda
import sorm.query.Query.Filter
import sorm.{Querier, Persisted}

/**
 * Created by dzhuraev on 4/20/16.
 */
case class History(staffer: Staffer, action: Int, action_date: DateTime, action_value: org.joda.time.LocalDate)

object History {
  implicit val historyFormat = Json.format[History]


  def findById(staffer: Staffer with Persisted) = {
    Db.query[History].whereEqual("staffer.id", staffer.id).order("id", true).fetch()
  }

  def getStaffActionsByDate(staffer: Staffer with Persisted, dataTime: Long) = {
    val bal = new LocalDate(dataTime)
    Db.query[History].whereEqual("staffer.id", staffer.id).whereEqual("action_value", bal).order("id", true).fetch()
  }

  def historyCount(staffer: Staffer with Persisted, date: Long) = {
    val bal = new LocalDate(date)
    Db.query[History].whereEqual("staffer.id", staffer.id).whereEqual("action_value", bal).count()
  }

  def historyGeneralCount() = {
    Db.query[History].count()
  }

}
