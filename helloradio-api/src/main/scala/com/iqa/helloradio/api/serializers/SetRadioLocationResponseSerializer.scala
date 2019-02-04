package com.iqa.helloradio.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{
  NegotiatedDeserializer,
  NegotiatedSerializer
}
import com.lightbend.lagom.scaladsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

import scala.collection.immutable

import ngcp.interview.interview.SetRadioLocationResponse

class SetRadioLocationResponseSerializer
    extends StrictMessageSerializer[SetRadioLocationResponse] {

  final private val serializer = {
    new NegotiatedSerializer[SetRadioLocationResponse, ByteString]() {
      override def protocol: MessageProtocol =
        MessageProtocol(Some("application/octet-stream"))

      def serialize(setRadioLocationResponse: SetRadioLocationResponse) = {
        val builder = ByteString.createBuilder
        setRadioLocationResponse.writeTo(builder.asOutputStream)
        builder.result
      }
    }
  }

  final private val deserializer = {
    new NegotiatedDeserializer[SetRadioLocationResponse, ByteString] {
      override def deserialize(bytes: ByteString) =
        SetRadioLocationResponse.parseFrom(bytes.iterator.asInputStream)
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
