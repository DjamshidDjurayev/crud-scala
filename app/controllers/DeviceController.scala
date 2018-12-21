  package controllers

  import models.Device
  import play.api.libs.json.Json
  import play.api.mvc._

  /**
   * Created by dzhuraev on 5/12/16.
   */
  class DeviceController extends Controller {

    def registerDevice(tokenId: String) = Action {
      Device.findDevice(tokenId).map {
        device => {
          Ok(Json.obj("status" -> "fail", "message" -> "device already exists"))
        }
      }.getOrElse {
        Device.saveDevice(tokenId)
        Ok(Json.obj("status" -> "success", "message" -> "device saved"))
      }
    }

    def removeDevice(tokenId: String) = Action {
      Device.findDevice(tokenId).map {
        device => {
          Device.removeDevice(device)
          Ok(Json.obj("status" -> "success", "message" -> "device removed"))
        }
      }.getOrElse {
        Ok(Json.obj("status" -> "fail", "message" -> "device not found"))
      }
    }

    def updateDevice(tokenId: String) = Action {
      Device.findDevice(tokenId).map {
        device => {
          Device.updateDevice(device)
          Ok(Json.obj("status" -> "success", "message" -> "device updated"))
        }
      }.getOrElse {
        Ok(Json.obj("status" -> "fail", "message" -> "device not found"))
      }
    }

    def getAllDevices() = Action {
      val devices = Device.getAllDevices()
      Ok(Json.toJson(devices))
    }

  }
