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
    // driver.getAllIssues should have size 0
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

      //outcome.replies should contain only "Hello, Alice!"
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

      //outcome.events should ===(List(AddedRadioProfileEvent(12,"first_radio_alias", RadioLocations(List("1_location","2_location","3_location","4_location")))),
      //                          AddRadioProfileCommand(34,"second_radio_alias",RadioLocations(List("5_location","6_location","7_location","8_location"))))

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
    /*
      val outcome = driver.run(ChangeBody("New body 1"), ChangeBody("New body 2"))
      outcome.events should ===(List(
        BodyChanged("post-1", "New body 1"),
        BodyChanged("post-1", "New body 2")))
      outcome.state.published should ===(false)
      outcome.state.content.get.body should ===("New body 2")
      outcome.replies should ===(List(Done, Done))

     */

    //outcome.replies should contain only "Hello, Alice!"

    //"answer" should ===("answeryyyy")
    //succeed
    }

    /*
    "allow updating the greeting message" in withTestDriver { driver =>
      val outcome1 = driver.run(UseGreetingMessage("Hi"))
      outcome1.events should contain only GreetingMessageChanged("Hi")
      val outcome2 = driver.run(Hello("Alice"))
      outcome2.replies should contain only "Hi, Alice!"
    }
   */
  }
}
