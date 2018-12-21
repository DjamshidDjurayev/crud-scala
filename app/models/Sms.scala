package models

import play.api.libs.json.Json

/**
 * Created by dzhuraev on 4/22/16.
 */
case class Sms(sender: String, message: String)

object Sms {
  implicit val smsFormat = Json.format[Sms]

  def getAllMessages() = {
    Db.query[Sms].order("id", true).fetch()
  }

  def saveMessage(sending: String, messaging: String) = {
    val msg = new Sms(sending, messaging)
    Db.save[Sms](msg)
  }
}