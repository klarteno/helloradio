package com.iqa.helloradio.impl

import com.iqa.helloradio.api
import com.iqa.helloradio.api.HelloradioService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import akka.Done

/**
  * Implementation of the HelloradioService.
  */
class HelloradioServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends HelloradioService {



  def addRadioItem(id: String): ServiceCall[AddRadioRequest, Done] = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[HelloradioEntity](id)

    ref.ask(AddRadioCommand(request.product))}

}

