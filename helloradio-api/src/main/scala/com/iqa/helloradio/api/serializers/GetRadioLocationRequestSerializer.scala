package com.iqa.helloradio.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{
  NegotiatedDeserializer,
  NegotiatedSerializer
}
import com.lightbend.lagom.scaladsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

import scala.collection.immutable

import ngcp.interview.interview.GetRadioLocationRequest

class GetRadioLocationRequestSerializer
    extends StrictMessageSerializer[GetRadioLocationRequest] {

  final private val serializer = {
    new NegotiatedSerializer[GetRadioLocationRequest, ByteString]() {
      override def protocol: MessageProtocol =
        MessageProtocol(Some("application/octet-stream"))

      def serialize(getRadioLocationRequest: GetRadioLocationRequest) = {
        val builder = ByteString.createBuilder
        getRadioLocationRequest.writeTo(builder.asOutputStream)
        builder.result
      }
    }
  }

  final private val deserializer = {
    new NegotiatedDeserializer[GetRadioLocationRequest, ByteString] {
      override def deserialize(bytes: ByteString) =
        GetRadioLocationRequest.parseFrom(bytes.iterator.asInputStream)
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
