package com.iqa.helloradio.api

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

import com.iqa.helloradio.api.serializers._
import ngcp.interview.interview.CreateRadioProfileRequest
import ngcp.interview.interview.CreateRadioProfileResponse

import collection.mutable.Stack
import org.scalatest._
import org.scalatest.Assertions._

class CreateRadioProfileSerializerTest
    extends FlatSpec
    with Matchers
    with OptionValues
    with Inside
    with Inspectors {

  "A Radio Profile Serializer" should "make serializations on the CreateRadioProfileRequest" in {
    val createRadioProfileRequest =
      CreateRadioProfileRequest(id = 13, "gdfgytryry", Seq("rtyrtyrtgsdg"))

    assertCompiles("new CreateRadioProfileReqSerializer()")

    val serializerFactory = new CreateRadioProfileReqSerializer()

    val serializedObject = serializerFactory.serializerForRequest.serialize(
      createRadioProfileRequest
    )
    val deSerializedObjectTemp = serializerFactory.deserializer(
      MessageProtocol(Some("application/octet-stream"))
    )
    val deSerializedObject =
      deSerializedObjectTemp.deserialize(serializedObject)

    assert(deSerializedObject == createRadioProfileRequest)

  }

  it should "make serializations on the CreateRadioProfileResponse" in {
    val createRadioProfileResponse = CreateRadioProfileResponse()

    assertCompiles("new CreateRadioProfileRespSerializer()")

    val serializerFactory = new CreateRadioProfileRespSerializer()

    val serializedObject = serializerFactory.serializerForRequest.serialize(
      createRadioProfileResponse
    )
    val deSerializedObjectTemp = serializerFactory.deserializer(
      MessageProtocol(Some("application/octet-stream"))
    )
    val deSerializedObject =
      deSerializedObjectTemp.deserialize(serializedObject)

    assert(deSerializedObject == createRadioProfileResponse)

  }

}
