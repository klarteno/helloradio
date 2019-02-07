package com.iqa.helloradio.impl

import scala.reflect.api.TypeTags
import scala.reflect.runtime.universe._

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.iqa.helloradio.api._

import ngcp.interview.interview._

class HelloradioServiceSpec
    extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll {

  def manOf[T: Manifest](t: T): Manifest[T] = manifest[T]

  def paramInfo[T](x: T)(implicit tag: TypeTag[T]): Unit = {
    val targs = tag.tpe match {
      case TypeRef(var1, var2, args) => (var1, var2, args)
    }
    info(s"type of $x has (type, concrete type ,arguments) : $targs")
  }
  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(true)
  ) { ctx =>
    new HelloradioApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[HelloradioService]

  override protected def beforeAll() = server
  override protected def afterAll() = server.stop()

  "HelloRadio service" should {

    //Method.POST, "/api/add-radio/:radioId/", createRadioProfileRequest
//radio_id, radio_alias, allowedLocations

    "reply with done when creating a radio profile request" in {

      var radioProfile = CreateRadioProfileRequest(
        12,
        "a_radio_alias",
        List(
          "one_location",
          "second_location",
          "third_location",
          "fourth_location"
        )
      )
      /*
      paramInfo(
        client
          .createRadioProfileRequest()
          .invoke(
            radioProfile
          )
      )
       */
      client
        .createRadioProfileRequest()
        .invoke(
          radioProfile
        )
        .map { answer =>
          //println("type of answer") // println has result type Unit

          //println(manOf(answer))

          //println(answer)

          answer should ===(akka.Done)

        }
      //outcome.events should ===(List(AddedRadioProfileEvent(12,"first_radio_alias", RadioLocations(List("1_location","2_location","3_location","4_location")))),
      //                          AddRadioProfileCommand(34,"second_radio_alias",RadioLocations(List("5_location","6_location","7_location","8_location"))))
      /*
      client
        .createRadioProfileRequest()
        .invoke(
          CreateRadioProfileRequest(
            34,
            "second_radio_alias",
            List("5_location", "6_location", "7_location", "8_location")
          )
        )
        .map { answer =>
          //println("type of answer") // println has result type Unit

          //println(manOf(answer))

          //println(answer)

          answer should ===(akka.Done)

          //succeed

        }
     */

    }

    "getting the correct radio profile" in {
      client
        .getRadioLocationRequest()
        .invoke(GetRadioLocationRequest(12))
        .map { answer =>
          //println("type of answer") // println has result type Unit

          //println(manOf(answer))
          /* info(
            "type of answer type of answer type of answer type of answer type of answer type of answer"
          )

          println(answer)
          println(manOf(answer))
          paramInfo(answer)
           */
          answer should not be (null)

          answer should ===(
            GetRadioLocationResponse().withLocation(
              "one_location"
            )
          )
        } /*

      client
        .setRadioLocationRequest(12, "new_mars_location")
        .invoke()
        .map { answer =>
          //println("type of answer") // println has result type Unit

          //println(manOf(answer))
           info(
            "type of answer type of answer type of answer type of answer type of answer type of answer"
          )

          println(answer)
          println(manOf(answer))
          paramInfo(answer)

          paramInfo(answer)
          answer should not be (null)

          answer should ===(
            SetRadioLocationResponse(
              true
            )
          )
        }
     */
    } /*

    "delete radio profile" in {

      client
        .createRadioProfileRequest()
        .invoke(
          CreateRadioProfileRequest(
            34,
            "second_radio_alias",
            List("5_location", "6_location", "7_location", "8_location")
          )
        )
        .map { answer =>
          //println("type of answer") // println has result type Unit

          //println(manOf(answer))

          //println(answer)

          answer should ===(akka.Done)

          //succeed

        }

      client
        .deleteRadioProfileRequest()
        .invoke(DeleteRadioProfileRequest(34))
        .map { answer =>
          //println("type of answer") // println has result type Unit

          //println(manOf(answer))
          info(
            "type of answer type of answer type of answer type of answer type of answer type of answer"
          )

          println(answer)
          println(manOf(answer))
          paramInfo(answer)

          answer should not be (null)

          answer should ===(akka.Done)

          // "asd" shouldBe "asd"

          // succeed
        }

    }*/
  }
}
