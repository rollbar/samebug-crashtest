package com.samebug.sandbox.crashtest

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class CrashReportTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    crasher.chainedException(List(3))
  }

  it("crashes with a cause chain") {
    crasher.chainedException(List(3,5,7))
  }

  private val crasher = new CrashReport
}
