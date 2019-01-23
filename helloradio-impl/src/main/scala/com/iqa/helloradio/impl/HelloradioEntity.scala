package com.iqa.helloradio.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

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
  override def initialState: HelloradioState = HelloradioState("",List.empty)

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case HelloradioState(alias , locations) => Actions()
      .onCommand[AddRadioProfileCommand, Done] {
      case (AddRadioProfileCommand(alias,location), context, state) =>
        context.thenPersist(
          AddedRadioProfileEvent(alias,location)
        ) { _ =>
          context.reply(Done)
        }
    }.onCommand[SetRadioLocationCommand, Done] {
      case (SetRadioLocationCommand(location), context, state) =>
        context.thenPersist(
          SetRadioLocationEvent(location)
        ) { _ =>
          context.reply(Done)
        }
    }.onCommand[RemoveRadioProfileCommand, Done] {
      case (RemoveRadioProfileCommand(alias), context, state) =>
        context.thenPersist(
          RemovedRadioProfileEvent(alias)
        ) { _ =>
          context.reply(Done)
        }
    }.onReadOnlyCommand[GetRadioLocationCommand.type, String] {
      case (GetRadioLocationCommand, context, state) => context.reply(state.locations(0))

    }.onEvent {
      case (AddedRadioProfileEvent(alias,location), state) =>
        HelloradioState(alias , location:: state.locations)
      case (SetRadioLocationEvent(location), state) =>
        HelloradioState(state.alias , location:: state.locations)
      case (RemovedRadioProfileEvent(alias_query), state) if state.alias == alias_query =>
        HelloradioState("",List.empty)

    }
  }
}


/**
  * The current state held by the persistent entity.
  */
case class HelloradioState(alias: String , locations: List[String])

object HelloradioState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[HelloradioState] = Json.format
}


/**
  * This interface defines all the events that the HelloradioEntity supports.
  */
sealed trait HelloradioEvent extends AggregateEvent[HelloradioEvent] {
  def aggregateTag = HelloradioEvent.Tag
}

object HelloradioEvent {
  val Tag = AggregateEventTag[HelloradioEvent]
}

case class AddedRadioProfileEvent(alias: String,location: String) extends HelloradioEvent
case class SetRadioLocationEvent(location: String) extends HelloradioEvent
case class RemovedRadioProfileEvent(alias_query: String) extends HelloradioEvent

/**
  * This interface defines all the commands that the HelloradioEntity supports.
  */
sealed trait HelloradioCommand[R] extends ReplyType[R]

final case class AddRadioProfileCommand(alias: String,location: String) extends HelloradioCommand[Done]
case class RemoveRadioProfileCommand(alias: String) extends HelloradioCommand[Done]
case class SetRadioLocationCommand(location: String) extends HelloradioCommand[Done]
case object GetRadioLocationCommand extends HelloradioCommand[String]


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
