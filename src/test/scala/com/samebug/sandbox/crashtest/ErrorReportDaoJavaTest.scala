package com.samebug.sandbox.crashtest

import com.google.inject.Guice
import com.mongodb.casbah.Imports.MongoDB
import com.samebug.modules.MockModule
import com.samebug.services.authentication.UserService
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import org.scalatest.{BeforeAndAfterAll, FunSpec}
import scala.collection.JavaConverters._

class ErrorReportDaoJavaTest extends FunSpec with BeforeAndAfterAll with LazyLogging {

  it("stores a report list") {
    pending
    val user = userService.user(2)
    val report = ErrorReport(null, user, None)
    val reports = Seq[ErrorReport](report).asJava
    dao.insert(reports)
  }

  it("stores another report list") {
    val user = userService.user(2)
    val report = ErrorReport(null, user, None)
    val reports = Seq[ErrorReport]().asJava
    dao.insert(reports)
  }

  private val module = new MockModule()
  private val injector = new ScalaInjector(Guice.createInjector(module))
  private val mongo = injector.instance[MongoDB]
  private val dao = injector.instance[ErrorReportDaoJava]
  private val userService = injector.instance[UserService]

  override def beforeAll() = {
    mongo.dropDatabase()
  }
}
