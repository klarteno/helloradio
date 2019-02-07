package com.iqa.helloradio.impl

import akka.{Done, NotUsed}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{
  EventStreamElement,
  PersistentEntityRegistry
}

import ngcp.interview.interview._
import com.iqa.helloradio.api
import com.iqa.helloradio.api.HelloradioService
import com.iqa.helloradio.api.models._

/**
  * Implementation of the HelloradioService.
  */
class HelloradioServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(
    implicit ec: ExecutionContext
) extends HelloradioService {

  override def createRadioProfileRequest()
      : ServiceCall[CreateRadioProfileRequest, Done] = ServiceCall {
    request: CreateRadioProfileRequest =>
      val ref =
        persistentEntityRegistry.refFor[HelloradioEntity](request.id.toString)
      ref.ask(
        AddRadioProfileCommand(
          request.id,
          request.alias,
          RadioLocations(request.allowedLocations.toList)
        )
      )
  }

  override def deleteRadioProfileRequest(
      ): ServiceCall[DeleteRadioProfileRequest, Done] = ServiceCall {
    request: DeleteRadioProfileRequest =>
      val ref =
        persistentEntityRegistry.refFor[HelloradioEntity](
          request.id.toString
        )
      ref.ask(RemoveRadioProfileCommand())
  }

  override def setRadioLocationRequest(
      radio_id: Long,
      location: String
  ): ServiceCall[NotUsed, SetRadioLocationResponse] = ServiceCall { _ =>
    val ref =
      persistentEntityRegistry.refFor[HelloradioEntity](radio_id.toString)
    ref.ask(SetRadioLocationCommand(location))
  }

  override def getRadioLocationRequest()
      : ServiceCall[GetRadioLocationRequest, GetRadioLocationResponse] =
    ServiceCall { request: GetRadioLocationRequest =>
      val ref =
        persistentEntityRegistry.refFor[HelloradioEntity](
          request.radioId.toString
        )
      ref.ask(GetRadioLocationCommand())
    }

  override def radioprofilesTopic(): Topic[RadioProfileMessage] = {
    TopicProducer.singleStreamWithOffset { offset =>
      persistentEntityRegistry
        .eventStream(HelloradioEvent.Tag, offset)
        .map(ev => (convertEvent(ev), offset))
    }
  }

  private def convertEvent(
      messageEvent: EventStreamElement[HelloradioEvent]
  ): RadioProfileMessage = {
    messageEvent.event match {
      case AddedRadioProfileEvent(
          radio_id,
          radio_alias,
          locations
          ) =>
        RadioProfileMessage(
          Some(radio_id),
          Some(radio_alias),
          Some(Locations(locations.allowedLocations))
        )
    }
  }
}
