package com.iqa.helloradio.api

import akka.{Done, NotUsed}
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{
  KafkaProperties,
  PartitionKeyStrategy
}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer

import com.iqa.helloradio.api.serializers._

import ngcp.interview.interview._

/**
  * The HelloRadio service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the HelloradioService.
  */
trait HelloradioService extends Service {
  def createRadioProfileRequest(): ServiceCall[CreateRadioProfileRequest, Done]
  def deleteRadioProfileRequest(
      ): ServiceCall[DeleteRadioProfileRequest, Done]
  def setRadioLocationRequest(
      radio_id: Long,
      location: String
  ): ServiceCall[NotUsed, SetRadioLocationResponse]
  def getRadioLocationRequest()
      : ServiceCall[GetRadioLocationRequest, GetRadioLocationResponse]

  override final def descriptor = {
    import Service._

    // @formatter:off
    named("helloradio")
      .withCalls(
        restCall(Method.POST, "/api/add-radio", createRadioProfileRequest _)(new CreateRadioProfileReqSerializer(),MessageSerializer.DoneMessageSerializer),
        //restCall(Method.POST, "/api/add-radio/:radioId/", createRadioProfileRequest _),
        restCall(Method.POST, "/api/radio/", deleteRadioProfileRequest _)(new DeleteRadioProfileReqSerializer(),MessageSerializer.DoneMessageSerializer),
        //restCall(Method.PUT, "/api/radio/:id/", setRadioLocationRequest _),
        pathCall("/api/radio/:id/:location", setRadioLocationRequest _)(MessageSerializer.NotUsedMessageSerializer,new SetRadioLocationResponseSerializer()),

        //restCall(Method.GET, "/api/radio/:id", getRadioLocationRequest _)(MessageSerializer.NotUsedMessageSerializer,new GetRadioLocationResponseSerializer())
        pathCall("/api/get-radio", getRadioLocationRequest _)(new GetRadioLocationRequestSerializer(),new GetRadioLocationResponseSerializer())
    )
      .withAutoAcl(true)
    // @formatter:on
  }
}
