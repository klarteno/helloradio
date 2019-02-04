package com.iqa.helloradio.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventTag,
  PersistentEntity
}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{
  JsonSerializer,
  JsonSerializerRegistry
}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

import ngcp.interview.interview.GetRadioLocationResponse
import ngcp.interview.interview.SetRadioLocationResponse

/**
  * This is an event sourced entity. It has a state, [[HelloradioState]], which
  * stores what the greeting should be (eg, "Hello").
  *
  * Event sourced entities are interacted with by sending them commands. This
  * entity supports two commands, a [[UseGreetingMessage]] command, which is
  * used to change the greeting, and a [[Hello]] command, which is a read
  * only command which returns a greeting to the name specified by the command.
  *
  * Commands get translated to events, and it's the events that get persisted by
  * the entity. Each event will have an event handler registered for it, and an
  * event handler simply applies an event to the current state. This will be done
  * when the event is first created, and it will also be done when the entity is
  * loaded from the database - each event will be replayed to recreate the state
  * of the entity.
  *
  * This entity defines one event, the [[GreetingMessageChanged]] event,
  * which is emitted when a [[UseGreetingMessage]] command is received.
  */
class HelloradioEntity extends PersistentEntity {

  override type Command = HelloradioCommand[_]
  override type Event = HelloradioEvent
  override type State = HelloradioState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: HelloradioState = HelloradioState.emptyRadio

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case state if (state.radio_id == None)  => initial
    case state if !(state.radio_id == None) => postAdded
  }

  private val initial: Actions = {
    Actions()
      .onCommand[AddRadioProfileCommand, Done] {
        case (
            AddRadioProfileCommand(radio_id, radio_alias, locations),
            context,
            state
            ) =>
          if (radio_id == 0 || radio_alias.equals("")) {
            context.invalidCommand("Radio Id and alias must be defined")
            context.done
          } else {
            context.thenPersist(
              AddedRadioProfileEvent(radio_id, radio_alias, locations)
            ) { _ =>
              context.reply(Done)
            }
          }
      }
      .onCommand[SetRadioLocationCommand, SetRadioLocationResponse] {
        case (SetRadioLocationCommand(location), context, state) =>
          context.thenPersist(
            EmptyEvent()
          ) { _ =>
            context.reply(SetRadioLocationResponse(false))
          }
      }
      .onEvent {
        case (
            AddedRadioProfileEvent(radio_id, radio_alias, locations),
            state
            ) =>
          HelloradioState(Some(radio_id), Some(radio_alias), Some(locations))
      }
  }

  private val postAdded: Actions = {
    Actions()
      .onCommand[SetRadioLocationCommand, SetRadioLocationResponse] {
        case (SetRadioLocationCommand(location), context, state) =>
          context.thenPersist(
            SetRadioLocationEvent(location)
          ) { _ =>
            context.reply(SetRadioLocationResponse(true))
          }
      }
      .onCommand[RemoveRadioProfileCommand, Done] {
        case (RemoveRadioProfileCommand(), context, state) =>
          context.thenPersist(
            RemovedRadioProfileEvent()
          ) { _ =>
            context.reply(Done)
          }
      }
      .onCommand[GetRadioLocationCommand, GetRadioLocationResponse] {
        case (GetRadioLocationCommand(), context, state) =>
          state.locations match {
            case Some(locs) =>
              context.thenPersist(EmptyEvent()) { _ =>
                context.reply(
                  GetRadioLocationResponse()
                    .withLocation(locs.allowedLocations(0))
                )
              }
            case _ =>
              context.thenPersist(EmptyEvent()) { _ =>
                context.reply(
                  GetRadioLocationResponse().withRadioNotFound(
                    GetRadioLocationResponse.RadioNotFound()
                  )
                )
              }
          }
      }
      .onEvent {
        case (SetRadioLocationEvent(location), state) =>
          // val locationsRadiosTemp = state.locations.toList.flatten ++ location
          HelloradioState(
            state.radio_id,
            state.radio_alias,
            Some(
              RadioLocations(location :: state.locations.get.allowedLocations)
            )
          )

        case (RemovedRadioProfileEvent(), state) =>
          HelloradioState.emptyRadio

        case (EmptyEvent(), state) =>
          HelloradioState(
            initialState.radio_id,
            initialState.radio_alias,
            initialState.locations
          )
      }
  }
}

final case class RadioLocations(allowedLocations: List[String])
object RadioLocations {
  implicit val format: Format[RadioLocations] = Json.format

}

/**
  * The current state held by the persistent entity.
  */
//final case class HelloradioState(radio_id: Long, radio_alias: String, allowedLocations: _root_.scala.collection.Seq[String])
final case class HelloradioState(
    radio_id: Option[Long],
    radio_alias: Option[String],
    locations: Option[RadioLocations]
)

object HelloradioState {
  val emptyRadio = HelloradioState(None, None, None)

  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val radioLocationsFormat = Json.format[RadioLocations]
  implicit val format: Format[HelloradioState] = Json.format
}

/**
  * This interface defines all the events that the HelloradioEntity supports.
  */
sealed trait HelloradioEvent extends AggregateEvent[HelloradioEvent] {
  override def aggregateTag = HelloradioEvent.Tag
}
final case class AddedRadioProfileEvent(
    radio_id: Long,
    radio_alias: String,
    locations: RadioLocations
) extends HelloradioEvent
final case class SetRadioLocationEvent(location: String) extends HelloradioEvent
final case class RemovedRadioProfileEvent() extends HelloradioEvent
final case class EmptyEvent() extends HelloradioEvent

object HelloradioEvent {
  val Tag = AggregateEventTag[HelloradioEvent]

  import play.api.libs.json._

  val serializers = Vector(
    JsonSerializer(Json.format[AddedRadioProfileEvent]),
    JsonSerializer(Json.format[SetRadioLocationEvent])
  )
}

/**
  * This interface defines all the commands that the HelloradioEntity supports.
  */
sealed trait HelloradioCommand[R] extends ReplyType[R]
final case class AddRadioProfileCommand(
    radio_id: Long,
    radio_alias: String,
    locations: RadioLocations
) extends HelloradioCommand[Done]
final case class RemoveRadioProfileCommand() extends HelloradioCommand[Done]
final case class SetRadioLocationCommand(location: String)
    extends HelloradioCommand[SetRadioLocationResponse]
final case class GetRadioLocationCommand()
    extends HelloradioCommand[GetRadioLocationResponse]
object HelloradioCommand {
  import play.api.libs.json._
  import JsonSerializer.emptySingletonFormat

  val serializers = Vector(
    JsonSerializer(Json.format[AddRadioProfileCommand]),
    JsonSerializer(Json.format[SetRadioLocationCommand])
  )
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object HelloradioSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[HelloradioState]
  )

}
