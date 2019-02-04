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
      .withCassandra()
  ) { ctx =>
    new HelloradioApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[HelloradioService]

  override protected def beforeAll() = server
  override protected def afterAll() = server.stop()

  "HelloRadio service" should {
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
      client
        .createRadioProfileRequest()
        .invoke(
          radioProfile
        )
        .map { answer =>
          answer should ===(akka.Done)
        }
    }

    "getting the correct radio profile" in {
      client
        .getRadioLocationRequest()
        .invoke(GetRadioLocationRequest(12))
        .map { answer =>
          answer should not be (null)

          answer should ===(
            GetRadioLocationResponse().withLocation(
              "one_location"
            )
          )
        }
    }
  }
}
