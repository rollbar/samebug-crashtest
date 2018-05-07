package com.samebug.crashtest.services
import ch.qos.logback.classic.LoggerContext
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.slf4j.LoggerFactory

class RegexCrasherTest extends FunSpec with BeforeAndAfterAll {
  it("crashes with bad group backreference") {
    sut.badGroupIndex()
  }

  private val sut = RegexCrasher
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
