package com.samebug.sandbox.crashtest

import java.util.regex.Pattern

import com.bugsnag.Bugsnag
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, FunSpec}

import scala.util.control.NonFatal



class CrashReportTest2 extends FunSpec with BeforeAndAfterAll with LazyLogging {
  it("simply crashes") {
    try {
      badRegex2()
    } catch {
      case NonFatal(x) => bugsnag.notify(x)
    }
  }

  def badRegex() = {
    "asd".replaceAll(".*", "$2")
  }

  def badRegex2() = {
    Pattern.compile(".*").matcher("asd").region(0,-1)
  }

  it("crashes with a cause chain") {
    try {
      badRegex2()
    } catch {
      case NonFatal(x) => bugsnag.notify(x)
    }
  }

  it("crashes with a class init") {
    try {
      crasher.failsToClInit()
    } catch {
      case x => bugsnag.notify(x)
    }
  }

  it("crashes with an init") {
    try {
      crasher.failsToInit()
    } catch {
      case NonFatal(x) => bugsnag.notify(x)
    }
  }

  val bugsnag = new Bugsnag("22103e8c010513782988c1684e3ba7d9")
  bugsnag.setProjectPackages("com.samebug")
  private val crasher = new CrashReport
}
