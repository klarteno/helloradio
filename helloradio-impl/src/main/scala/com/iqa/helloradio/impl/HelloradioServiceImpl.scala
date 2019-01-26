package ngcp.com.iqa.helloradio.impl

import akka.{Done, NotUsed}

import ngcp.com.iqa.helloradio.api
import ngcp.com.iqa.helloradio.api.HelloradioService
import ngcp.com.iqa.helloradio.api.AddRadioProfileRequest
import ngcp.com.iqa.helloradio.api.SetRadioLocationRequest
import ngcp.com.iqa.helloradio.api.RemoveRadioProfileRequest
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the HelloradioService.
  */
class HelloradioServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends HelloradioService {

  override def createRadioProfileRequest(radioId: Long): ServiceCall[AddRadioProfileRequest, Done] = ServiceCall { request:AddRadioProfileRequest =>
    val ref = persistentEntityRegistry.refFor[HelloradioEntity](radioId.toString)

    ref.ask(AddRadioProfileCommand(request.alias,request.location))}


  override def deleteRadioProfileRequest(radio_id: Long): ServiceCall[RemoveRadioProfileRequest, Done] = ServiceCall { request:RemoveRadioProfileRequest =>
    val ref = persistentEntityRegistry.refFor[HelloradioEntity](radio_id.toString)

    ref.ask(RemoveRadioProfileCommand(request.alias))
  }


  override def setRadioLocationRequest(radio_id:Long): ServiceCall[SetRadioLocationRequest, Done] = ServiceCall { request:SetRadioLocationRequest =>
    val ref = persistentEntityRegistry.refFor[HelloradioEntity](radio_id.toString)

    ref.ask(SetRadioLocationCommand(request.location))
  }


  override  def getRadioLocationRequest(radio_id:Long): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[HelloradioEntity](radio_id.toString)

    ref.ask(GetRadioLocationCommand)
  }


}

