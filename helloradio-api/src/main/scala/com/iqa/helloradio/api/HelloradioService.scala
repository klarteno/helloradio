package com.iqa.helloradio.api

import akka.{Done, NotUsed}
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}


/**
  * The HelloRadio service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the HelloradioService.
  */
trait HelloradioService extends Service {

  def createRadioProfileRequest(radioId: Long): ServiceCall[AddRadioProfileRequest, Done]
  def deleteRadioProfileRequest(radio_id: Long): ServiceCall[RemoveRadioProfileRequest, Done]
  def setRadioLocationRequest(radio_id:Long): ServiceCall[SetRadioLocationRequest, Done]
  def getRadioLocationRequest(radio_id:Long): ServiceCall[NotUsed, String]


  override final def descriptor = {
    import Service._

    // @formatter:off
    named("helloradio")
      .withCalls(
        restCall(Method.POST, "/api/add-radio/:radioId/", createRadioProfileRequest _),
        restCall(Method.POST, "/api/radio/:id", deleteRadioProfileRequest _),
        restCall(Method.PUT, "/api/radio/:id/", setRadioLocationRequest _),
        restCall(Method.GET, "/api/radio/:id", getRadioLocationRequest _)
    )
      .withAutoAcl(true)
    // @formatter:on
  }
}


case class AddRadioProfileRequest(alias: String,location: String)
object AddRadioProfileRequest {
  implicit val format: Format[AddRadioProfileRequest] =
    Json.format[AddRadioProfileRequest]
}

case class RemoveRadioProfileRequest(alias: String)
object RemoveRadioProfileRequest {
  implicit val format: Format[RemoveRadioProfileRequest] = Json.format[RemoveRadioProfileRequest]
}

case class SetRadioLocationRequest(location: String)
object SetRadioLocationRequest {
  implicit val format: Format[SetRadioLocationRequest] = Json.format[SetRadioLocationRequest]
}
