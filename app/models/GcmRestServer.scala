package models

import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WS

/**
 * Created by dzhuraev on 5/11/16.
 */
class GcmRestServer(val key: String) {

  def send(ids: List[String], data: Map[String, String]) = {

    import play.api.Play.current

    import scala.concurrent.ExecutionContext.Implicits.global

    val body = Json.obj(
      "registration_ids" -> ids,
      "data" -> data
    )

    WS.url("https://android.googleapis.com/gcm/send")
      .withHeaders(
        "Authorization" -> s"key=$key",
        "Content-type" -> "application/json"
      )
      .post(body)
      .map { response =>
      Logger.debug("Result: " + response.body)
    }
  }
}
