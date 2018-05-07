package com.samebug.crashtest.services

import com.google.inject.Guice
import com.samebug.crashtest.modules.{MongoModuleConfig, MysqlModuleConfig, TestModule}
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory

class MysqlCrasherTest extends FunSpec with BeforeAndAfterAll {

  it("connects to mysql") {
    sut.connect()
  }

  private val module = new TestModule(MysqlModuleConfig("localhost", 3306), MongoModuleConfig("localhost", 27017))
  private val injector = new ScalaInjector(Guice.createInjector(module))
  private val sut = injector.instance[MysqlCrasher]
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
