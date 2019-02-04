package com.iqa.helloradio.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{
  NegotiatedDeserializer,
  NegotiatedSerializer
}
import com.lightbend.lagom.scaladsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

import scala.collection.immutable

import ngcp.interview.interview.DeleteRadioProfileRequest

class DeleteRadioProfileReqSerializer
    extends StrictMessageSerializer[DeleteRadioProfileRequest] {

  final private val serializer = {
    new NegotiatedSerializer[DeleteRadioProfileRequest, ByteString]() {
      override def protocol: MessageProtocol =
        MessageProtocol(Some("application/octet-stream"))

      def serialize(deleteRadioProfileRequest: DeleteRadioProfileRequest) = {
        val builder = ByteString.createBuilder
        deleteRadioProfileRequest.writeTo(builder.asOutputStream)
        builder.result
      }
    }
  }

  final private val deserializer = {
    new NegotiatedDeserializer[DeleteRadioProfileRequest, ByteString] {
      override def deserialize(bytes: ByteString) =
        DeleteRadioProfileRequest.parseFrom(bytes.iterator.asInputStream)
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
