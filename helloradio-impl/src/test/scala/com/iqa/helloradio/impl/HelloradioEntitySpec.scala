package com.iqa.helloradio.impl

import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
//import akka.testkit.javadsl.TestKit
import akka.testkit.TestKit
import akka.actor.ActorSystem

import org.scalatest.{AsyncWordSpec, WordSpec, BeforeAndAfterAll, Matchers}

class HelloradioEntitySpec
    extends WordSpec
    with Matchers
    with BeforeAndAfterAll {

  def manOf[T: Manifest](t: T): Manifest[T] = manifest[T]

  private val system = ActorSystem(
    "HelloradioEntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(HelloradioSerializerRegistry)
  )

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private def withTestDriver(
      block: PersistentEntityTestDriver[HelloradioCommand[_], HelloradioEvent, HelloradioState] => Unit
  ): Unit = {
    val driver = new PersistentEntityTestDriver(
      system,
      new HelloradioEntity,
      "helloradio-1"
    )
    block(driver)
  }

  "HelloRadio entity" should {

    "create radio profile request commnad" in withTestDriver { driver =>
      val outcome = driver.run(
        AddRadioProfileCommand(
          12,
          "first_radio_alias",
          RadioLocations(
            List("1_location", "2_location", "3_location", "4_location")
          )
        )
      )

      println("outcome.replies")
      println(outcome.replies)
      println(manOf(outcome.replies))

      println("outcome.state")
      println(outcome.state)
      println(manOf(outcome.state))

      println("outcome.events")
      println(outcome.events)
      println(manOf(outcome.events))

      println("outcome.issues")
      println(outcome.issues)
      println(manOf(outcome.issues))

      outcome.events should ===(
        List(
          AddedRadioProfileEvent(
            12,
            "first_radio_alias",
            RadioLocations(
              List("1_location", "2_location", "3_location", "4_location")
            )
          )
        )
      )

      outcome.state.radio_id should ===(Some(12))
      outcome.replies should ===(List(akka.Done))

      driver.run(
        AddRadioProfileCommand(
          34,
          "second_radio_alias",
          RadioLocations(
            List("5_location", "6_location", "7_location", "8_location")
          )
        )
      )

      outcome.events should ===(
        List(
          AddedRadioProfileEvent(
            12,
            "first_radio_alias",
            RadioLocations(
              List("1_location", "2_location", "3_location", "4_location")
            )
          )
        )
      )

      outcome.state.radio_id should ===(Some(12))

      outcome.replies should ===(List(akka.Done))

    }
  }
}
