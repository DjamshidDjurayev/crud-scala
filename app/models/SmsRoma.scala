package models

import play.api.libs.json.Json

/**
 * Created by dzhuraev on 4/22/16.
 */
case class SmsRoma(sender: String, message: String)

object SmsRoma {
  implicit val smsFormat = Json.format[SmsRoma]

  def getAllMessages() = {
    Db.query[SmsRoma].order("id", true).fetch()
  }

  def saveMessage(sending: String, messaging: String) = {
    val msg = new SmsRoma(sending, messaging)
    Db.save[SmsRoma](msg)
  }
}