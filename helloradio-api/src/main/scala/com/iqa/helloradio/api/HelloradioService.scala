package com.iqa.helloradio.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}



/**
  * The HelloRadio service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the HelloradioService.
  */
trait HelloradioService extends Service {

  def addRadioItem(id: String): ServiceCall[AddRadioRequest, Done]

  override final def descriptor = {
    import Service._

    // @formatter:off
    named("helloradio")
      .withCalls(
        restCall(Method.POST, “/api/add-radio/:id”, addRadioItem _)
    //restCall(Method.POST, “/api/add-to-cart/:id”, addToCart _),

    )
      .withAutoAcl(true)
    // @formatter:on
  }
}


case class AddRadioRequest(product: String)
object AddRadioRequest {
  implicit val format: Format[AddRadioRequest] =
    Json.format[AddRadioRequest]
}




