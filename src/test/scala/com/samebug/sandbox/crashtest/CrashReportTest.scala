package com.samebug.sandbox.crashtest

import com.typesafe.scalalogging.LazyLogging
import io.airbrake.javabrake.Notifier
import org.scalatest.{BeforeAndAfterAll, FunSpec}

import scala.util.control.NonFatal

class CrashReportTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    try {
      crasher.chainedException(List(3))
    } catch {
      case NonFatal(x) => notifier.report(x).get()
    }
  }

  it("crashes with a cause chain") {
    try {
      crasher.chainedException(List(3,5,7))
    } catch {
      case NonFatal(x) => notifier.report(x).get()
    }
  }


  val projectId = 167586
  val projectKey = "c085bfb7104ea33821a2dfa8ed501184"
  val notifier = new Notifier(projectId, projectKey)
  private val crasher = new CrashReport
}
