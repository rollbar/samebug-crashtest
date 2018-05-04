package com.samebug.crashtest.services

import com.google.inject.Guice
import com.samebug.crashtest.modules.{MongoModuleConfig, MysqlModuleConfig, TestModule}
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class MysqlCrasherTest extends FunSpec with BeforeAndAfterAll {

  it("connects to mysql") {
    sut.connect()
  }

  private val module = new TestModule(MysqlModuleConfig("localhost", 33306), MongoModuleConfig("localhost", 27017))
  private val injector = new ScalaInjector(Guice.createInjector(module))
  private val sut = injector.instance[MysqlCrasher]

  override def afterAll() = {
  }
}
