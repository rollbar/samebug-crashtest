package com.samebug.crashtest.services

import javax.inject.Inject

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.casbah.Imports.MongoDB
import com.samebug.crashtest.entities.TestEntity
import org.mongojack.JacksonDBCollection

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

class MongoCrasher @Inject()(protected val mongodb: MongoDB, protected val mapper: ObjectMapper) extends Reporter {

  def failToInsertEmptyList() = {
    val items: Seq[TestEntity] = Nil
    try {
      collection.insert(items.asJava)
    } catch {
      case NonFatal(x) => report(x)
    }
  }

  private val rawCollection = mongodb.getCollection("test")
  private val collection = JacksonDBCollection.wrap(rawCollection, classOf[TestEntity], classOf[Int], mapper)
}
