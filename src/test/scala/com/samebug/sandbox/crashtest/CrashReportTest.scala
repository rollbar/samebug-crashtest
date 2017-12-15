package com.samebug.sandbox.crashtest

import com.mindscapehq.raygun4java.core.RaygunClient
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, FunSpec}

import scala.util.control.NonFatal

class CrashReportTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    try {
      crasher.chainedException(List(3))
    } catch {
      case NonFatal(x) => client.Send(x)
    }
  }

  it("crashes with a cause chain") {
    try {
      crasher.chainedException(List(3,5,7))
    } catch {
      case NonFatal(x) => client.Send(x)
    }
  }

  val client = new RaygunClient("CF7+TV/W2Qstz0VYEIOq9g==")
  private val crasher = new CrashReport
}
