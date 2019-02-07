package com.iqa.helloradio.api.models

import play.api.libs.json.{Format, Json}

final case class RadioProfileMessage(
    radio_id: Option[Long],
    radio_alias: Option[String],
    locations: Option[Locations]
)

object RadioProfileMessage {
  //val emptyRadioProfileMessage = RadioProfileMessage(None, None, None)
  implicit val radioLocationsFormat = Json.format[Locations]
  implicit val format: Format[RadioProfileMessage] = Json.format
}

final case class Locations(allowedLocations: List[String])
object Locations {
  implicit val format: Format[Locations] = Json.format

}
