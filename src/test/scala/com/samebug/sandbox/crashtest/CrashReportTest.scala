package com.samebug.sandbox.crashtest

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, FunSpec}

import scala.util.control.NonFatal

class CrashReportTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    try {
      crasher.chainedException(List(3))
    } catch {
      case NonFatal(x) => logger.error("other nasty failure", x)
    }
  }

  it("crashes with a cause chain") {
    try {
      crasher.chainedException(List(3,5,7))
    } catch {
      case NonFatal(x) => throw x
    }
  }

  private val crasher = new CrashReport
}
