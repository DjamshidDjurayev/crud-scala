package models

import play.api.libs.json.Json

/**
 * Created by dzhuraev on 3/30/16.
 */
case class Positions(title: String)

object Positions {
  implicit val positionsFormat = Json.format[Positions]

  def getAllPositions: Seq[(String, String)] = {
    Db.query[Positions].fetch().map(c => (c.title, c.title))
  }

  def findByTitle(title: String) = {
    Db.query[Positions].whereEqual("title", title).fetchOne()
  }


  def delete(positions: Positions) = {
    Db.delete[Positions](positions)
  }

  def update(positions: Positions, title: String) = {
    Db.save(positions.copy(title = title))
  }

  def positionCount() = {
    Db.query[Positions].count()
  }

}
