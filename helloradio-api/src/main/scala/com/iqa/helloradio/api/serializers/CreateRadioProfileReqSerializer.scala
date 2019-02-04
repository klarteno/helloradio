package com.iqa.helloradio.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{
  NegotiatedDeserializer,
  NegotiatedSerializer
}
import com.lightbend.lagom.scaladsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

import scala.collection.immutable

import ngcp.interview.interview.CreateRadioProfileRequest

class CreateRadioProfileReqSerializer
    extends StrictMessageSerializer[CreateRadioProfileRequest] {

  final private val serializer = {
    new NegotiatedSerializer[CreateRadioProfileRequest, ByteString]() {
      override def protocol: MessageProtocol =
        MessageProtocol(Some("application/octet-stream"))

      def serialize(createRadioProfileRequest: CreateRadioProfileRequest) = {
        val builder = ByteString.createBuilder
        createRadioProfileRequest.writeTo(builder.asOutputStream)
        builder.result
      }
    }
  }

  final private val deserializer = {
    new NegotiatedDeserializer[CreateRadioProfileRequest, ByteString] {
      override def deserialize(bytes: ByteString) =
        CreateRadioProfileRequest.parseFrom(bytes.iterator.asInputStream)
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
