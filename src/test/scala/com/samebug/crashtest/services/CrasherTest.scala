package com.samebug.crashtest.services

import ch.qos.logback.classic.LoggerContext
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

class CrasherTest extends FunSpec with BeforeAndAfterAll with LazyLogging {
  ignore("simply crashes") {
    try {
      sut.chainedException(List(3))
    } catch {
      case NonFatal(x) => logger.error("other nasty failure", x)
    }
  }

  ignore("crashes with a cause chain") {
    try {
      sut.chainedException(List(3,5,7))
    } catch {
      case NonFatal(x) => throw x
    }
  }

  private val sut = new Crasher
  private val loggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]

  override def beforeAll() = {
    loggerContext.start()
  }
  override def afterAll() = {
    Thread.sleep(1000)
    loggerContext.stop()
    Thread.sleep(1000)
  }
}
