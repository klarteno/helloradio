package com.iqa.helloradio.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{
  NegotiatedDeserializer,
  NegotiatedSerializer
}
import com.lightbend.lagom.scaladsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

import scala.collection.immutable

import ngcp.interview.interview.CreateRadioProfileResponse

class CreateRadioProfileRespSerializer
    extends StrictMessageSerializer[CreateRadioProfileResponse] {

  final private val serializer = {
    new NegotiatedSerializer[CreateRadioProfileResponse, ByteString]() {
      override def protocol: MessageProtocol =
        MessageProtocol(Some("application/octet-stream"))

      def serialize(createRadioProfileResponse: CreateRadioProfileResponse) = {
        val builder = ByteString.createBuilder
        createRadioProfileResponse.writeTo(builder.asOutputStream)
        builder.result
      }
    }
  }

  final private val deserializer = {
    new NegotiatedDeserializer[CreateRadioProfileResponse, ByteString] {
      override def deserialize(bytes: ByteString) =
        CreateRadioProfileResponse.parseFrom(bytes.iterator.asInputStream)
    }
  }

  override def serializerForRequest =
    serializer
  override def deserializer(protocol: MessageProtocol) =
    deserializer
  override def serializerForResponse(
      acceptedMessageProtocols: immutable.Seq[MessageProtocol]
  ) = serializer
}
