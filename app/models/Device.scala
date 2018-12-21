package models

import play.api.libs.json.Json

/**
 * Created by dzhuraev on 5/12/16.
 */
case class Device(token: String)

object Device {

  implicit val format = Json.format[Device]

  def getAllDevices() = {
    Db.query[Device].fetch()
  }

  def saveDevice(tokenId: String) = {
    val device = new Device(tokenId)
    Db.save[Device](device)
  }

  def removeDevice(device: Device) = {
    Db.delete[Device](device)
  }

  def updateDevice(device: Device) = {
    Db.save[Device](device)
  }

  def findDevice(tokenId: String) = {
    Db.query[Device].whereEqual("token", tokenId).fetchOne()
  }

}
