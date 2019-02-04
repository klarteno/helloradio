package com.iqa.helloradio.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{
  NegotiatedDeserializer,
  NegotiatedSerializer
}
import com.lightbend.lagom.scaladsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

import scala.collection.immutable

import ngcp.interview.interview.GetRadioLocationResponse

class GetRadioLocationResponseSerializer
    extends StrictMessageSerializer[GetRadioLocationResponse] {

  final private val serializer = {
    new NegotiatedSerializer[GetRadioLocationResponse, ByteString]() {
      override def protocol: MessageProtocol =
        MessageProtocol(Some("application/octet-stream"))

      def serialize(getRadioLocationResponse: GetRadioLocationResponse) = {
        val builder = ByteString.createBuilder
        getRadioLocationResponse.writeTo(builder.asOutputStream)
        builder.result
      }
    }
  }

  final private val deserializer = {
    new NegotiatedDeserializer[GetRadioLocationResponse, ByteString] {
      override def deserialize(bytes: ByteString) =
        GetRadioLocationResponse.parseFrom(bytes.iterator.asInputStream)
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
