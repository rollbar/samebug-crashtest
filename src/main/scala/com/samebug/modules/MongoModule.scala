package com.samebug.modules

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.google.inject.AbstractModule
import com.mongodb.casbah.Imports.{MongoClient, MongoDB}
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.ScalaModule

class MongoModule() extends AbstractModule with ScalaModule with LazyLogging {

  override def configure(): Unit = {
    bind[MongoDB].toInstance(database)
    bind[MongoClient].toInstance(client)
    bind[ObjectMapper].toInstance(mapper)
  }

  private lazy val client = MongoClient("localhost", 27017)
  private lazy val database = client.getDB("test")
  private lazy val mapper = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.setSerializationInclusion(Include.NON_ABSENT)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    mapper
  }
}
