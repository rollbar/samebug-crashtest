package com.samebug.sandbox.crashtest

import javax.inject.Inject

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.mongodb.casbah.Imports.MongoDB
import com.samebug.database.mongo.MongoCollection
import com.samebug.entities.User
import com.samebug.entities.jvm.NormalizedStackTrace
import org.mongojack.WriteResult
import org.mongojack.internal.{ObjectIdDeserializers, ObjectIdSerializer}

import scala.collection.JavaConverters._



class ErrorReportDao @Inject()(protected val mongodb: MongoDB, protected val mapper: ObjectMapper) extends MongoCollection[ErrorReport, String] {

  def insert(report: ErrorReport): ErrorReport =  {
    val writeResult = collection.insert(report copy (id = null))
    writeResult.getSavedObject
  }

  def insert(reports: Seq[ErrorReport]): Seq[ErrorReport] =  {
    val writeResult: WriteResult[ErrorReport, String] = collection.insert(reports.asJava)
    writeResult.getSavedObjects.asScala
  }

  override protected def collectionName = "errors_reports"
}


case class ErrorReport(
  @JsonSerialize(using = classOf[ObjectIdSerializer])
  @JsonDeserialize(using = classOf[ObjectIdDeserializers.ToStringDeserializer])
  @JsonProperty("_id")
  id: String,
  user: User,
  stacktrace: Option[NormalizedStackTrace]
)

