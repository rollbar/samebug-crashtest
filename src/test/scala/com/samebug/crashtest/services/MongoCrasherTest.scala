package com.samebug.crashtest.services

import ch.qos.logback.classic.LoggerContext
import com.google.inject.Guice
import com.mongodb.casbah.Imports.MongoDB
import com.samebug.crashtest.modules.{MongoModuleConfig, MysqlModuleConfig, TestModule}
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import org.slf4j.LoggerFactory

class MongoCrasherTest extends FunSpec with BeforeAndAfterAll {

  it("fails to insert empty list") {
    sut.failToInsertEmptyList()
  }

  private val module = new TestModule(MysqlModuleConfig("localhost", 3306), MongoModuleConfig("localhost", 2717))
  private val injector = new ScalaInjector(Guice.createInjector(module))
  private val mongo = injector.instance[MongoDB]
  private val sut = injector.instance[MongoCrasher]

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
