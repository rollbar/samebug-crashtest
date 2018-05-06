package com.samebug.crashtest.services

import com.google.inject.Guice
import com.mongodb.casbah.Imports.MongoDB
import com.samebug.crashtest.modules.{MongoModuleConfig, MysqlModuleConfig, TestModule}
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class MongoCrasherTest extends FunSpec with BeforeAndAfterAll {

  it("fails to insert empty list") {
    sut.failToInsertEmptyList()
  }

  private val module = new TestModule(MysqlModuleConfig("localhost", 3306), MongoModuleConfig("localhost", 27017))
  private val injector = new ScalaInjector(Guice.createInjector(module))
  private val mongo = injector.instance[MongoDB]
  private val sut = injector.instance[MongoCrasher]

  override def beforeAll() = {
    mongo.dropDatabase()
  }
}
