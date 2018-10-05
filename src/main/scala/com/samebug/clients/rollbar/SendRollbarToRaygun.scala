package com.samebug.clients.rollbar

import java.io.File
import java.net.InetAddress

import com.mindscapehq.raygun4java.core.messages.{RaygunErrorMessage, RaygunErrorStackTraceLineMessage, RaygunMessage}
import com.mindscapehq.raygun4java.core.{RaygunClient, RaygunMessageBuilder}
import com.samebug.clients.rollbar.entities._
import com.samebug.clients.rollbar.mapping.RollbarProtocol._
import spray.json._

import scala.io.Source

object SendRollbarToRaygun extends App {
  val eventFolder = "/data/samebug/rollbar/818669c8d222442eb64e631ed51fc548"
  val itemsFolder = eventFolder + "/item"
  val occurrenceFiles = new File(itemsFolder).list() map { itemFolder => s"$itemsFolder/$itemFolder/occurrences.json" }
  val client = new RaygunClient("dCGTXotsu6PuCOmeZfNiOg==")

  occurrenceFiles.take(1) foreach { occurrenceFile =>
    val json = Source.fromFile(occurrenceFile).mkString
    val result = json.parseJson.asJsObject.fields("result").convertTo[InstancesResult]

    result.instances.filter {_.data.language == Language.Java }.foreach { instance =>
      val body = instance.data.body
      val raygunMessage = makeMessage(body)
      client.Post(raygunMessage)
    }
  }

  def makeMessage(body: InstanceBody): RaygunMessage = {
    val defaultMsg = RaygunMessageBuilder.New
      .SetEnvironmentDetails
      .SetMachineName(InetAddress.getLocalHost.getHostName)
      .SetClientDetails
      .Build
    defaultMsg.getDetails.setError(convert(body))
    defaultMsg
  }

  def convert(body: InstanceBody): RaygunErrorMessage = {
    body match {
      case t: TraceBody => convert(t.trace)
      case t: TraceChainBody =>
        val parts = t.traceChain map convert
        parts.reduceRight { (builder, cause) => builder.setInnerError(cause); builder }
    }
  }

  def defaultError = new RaygunErrorMessage(new Throwable)

  def convert(x: TracePart): RaygunErrorMessage = {
    val e = defaultError
    e.setMessage(x.exception.message.orNull)
    e.setClassName(x.exception.typeName)
    e.setStackTrace(x.frames.map(convert).toArray)
    e
  }

  def convert(x: Frame): RaygunErrorStackTraceLineMessage = {
    val ste = new StackTraceElement(x.fullyQualifiedClassName.orNull, x.method.orNull, x.fileName.orNull, x.lineNumber.getOrElse(-1))
    new RaygunErrorStackTraceLineMessage(ste)
  }
}
