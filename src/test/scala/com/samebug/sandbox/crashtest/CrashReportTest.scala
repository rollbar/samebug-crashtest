package com.samebug.sandbox.crashtest

import com.typesafe.scalalogging.LazyLogging
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import org.scalatest.{BeforeAndAfterAll, FunSpec}

import scala.util.control.NonFatal

class CrashReportTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    try {
      Sentry.getContext.recordBreadcrumb(new BreadcrumbBuilder().setMessage("User made an action").build)
      Sentry.capture("This is a test")
      crasher.chainedException(List(3))
    } catch {
      case NonFatal(x) => Sentry.capture(x)
    }
  }

  it("crashes with a cause chain") {
    try {
      crasher.chainedException(List(3, 5, 7))
    } catch {
      case NonFatal(x) => Sentry.capture(x)
    }
  }

  private val crasher = new CrashReport
  Sentry.init("https://e2c242ae5fa34ce18bd330d70d0dfb51:e2700e514305434c9a0fa49a78222c22@sentry.io/1241256")
}
