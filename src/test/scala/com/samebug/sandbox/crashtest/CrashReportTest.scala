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
  Sentry.init("https://1211268ba00f4d88bf6be37f4d0d1fbc:b9a94423a3054600b4e8599e44b5ebcb@sentry.io/259617")
}
