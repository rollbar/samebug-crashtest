package com.samebug.sandbox.crashtest

import com.bugsnag.Bugsnag
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, FunSpec}

import scala.util.control.NonFatal

class CrashReportTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    try {
      crasher.chainedException(List(3))
    } catch {
      case NonFatal(x) => bugsnag.notify(x)
    }
  }

  it("crashes with a cause chain") {
    try {
      crasher.chainedException(List(3,5,7))
    } catch {
      case NonFatal(x) => bugsnag.notify(x)
    }
  }

  val bugsnag = new Bugsnag("fb25026742539586f58eed8675a060b1")
  private val crasher = new CrashReport
}
