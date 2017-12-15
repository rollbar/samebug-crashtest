package com.samebug.sandbox.crashtest

import com.rollbar.notifier.Rollbar
import com.rollbar.notifier.config.ConfigBuilder
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, FunSpec}

import scala.util.control.NonFatal

class CrashReportTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    try {
      crasher.chainedException(List(3))
    } catch {
      case NonFatal(x) => rollbar.error(x)
    }
  }

  it("crashes with a cause chain") {
    try {
      crasher.chainedException(List(3,5,7))
    } catch {
      case NonFatal(x) => rollbar.error(x)
    }
  }

  private val crasher = new CrashReport
  private val rollbar = {
    val config = ConfigBuilder
      .withAccessToken(System.getenv("ROLLBAR_ACCESSTOKEN"))
      .environment("development")
      .codeVersion("1.0.0")
      .build
    Rollbar.init(config)
  }
}
