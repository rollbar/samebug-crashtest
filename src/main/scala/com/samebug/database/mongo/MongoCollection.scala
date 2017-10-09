package com.samebug.database.mongo

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.casbah.Imports._
import com.samebug.database.mongo.MongoCollection._
import org.mongojack.{Aggregation, DBCursor, DBUpdate, JacksonDBCollection}

import scala.collection.JavaConverters._
import scala.concurrent.blocking
import scala.reflect.{ClassTag, classTag}

abstract class MongoCollection[ItemType, IdType](implicit itemTypeTag: ClassTag[ItemType], idTypeTag: ClassTag[IdType]) {
  protected def collectionName: String

  protected def mongodb: MongoDB

  protected def mapper: ObjectMapper

  protected val emptyObject = MongoDBObject()

  protected final def serializeDocumentUpdate[T: ClassTag](update: DBUpdate.Builder): DBObject = update.serialiseAndGet(mapper, mapper.constructType(classTag[T].runtimeClass))

  final def get(id: IdType): Option[ItemType] = blocking {Option(collection.findOneById(id))}

  final def apply(id: IdType): ItemType = get(id) getOrElse {throw new NoSuchElementException(s"No item with id $id")}

  final def findByIds(ids: Seq[IdType])(extractId: ItemType => IdType): Map[IdType, ItemType] = blocking {
    find("_id" $in ids).map { s => extractId(s) -> s }.toMap
  }

  final def find(query: DBObject, offset: Int, maxHits: Int): Seq[ItemType] = blocking {collection.find(query).skip(offset).limit(maxHits).toSeq}

  final def find(query: DBObject, keys: DBObject, offset: Int, maxHits: Int): Seq[ItemType] = blocking {collection.find(query, keys).skip(offset).limit(maxHits).toSeq}

  final def find(query: DBObject): Seq[ItemType] = blocking {collection.find(query).toSeq}

  final def find(query: DBObject, keys: DBObject): Seq[ItemType] = blocking {collection.find(query, keys).toSeq}

  final def findOne(query: DBObject): Option[ItemType] = blocking {Option(collection.findOne(query))}

  final def findOne(query: DBObject, keys: DBObject): Option[ItemType] = blocking {Option(collection.findOne(query, keys))}

  final def aggregate[T](aggregation: Aggregation[T]): Seq[T] = blocking {collection.aggregate(aggregation).results().asScala}

  private val itemType: Class[ItemType] = itemTypeTag.runtimeClass.asInstanceOf[Class[ItemType]]

  private val idType: Class[IdType] = idTypeTag.runtimeClass.asInstanceOf[Class[IdType]]

  final val collection: JacksonDBCollection[ItemType, IdType] =
    JacksonDBCollection.wrap(mongodb.getCollection(collectionName), itemType, idType, mapper)
}

trait MongoCounter {
  protected def counterName: String

  protected def mongodb: MongoDB

  def nextId: Int = {
    val increasedNum = collection.findAndModify("_id" $eq counterName, null, null, false, $inc("seq" -> 1), true, true)
    increasedNum.get("seq").asInstanceOf[Any] match {
      case i: Int => i.intValue()
      case d: Double => d.intValue()
      case l: Long => l.intValue()
      case _ => throw new IllegalArgumentException(s"Invalid type for a sequence $counterName")
    }
  }

  private val collection = mongodb.getCollection("counters")
}

abstract class AutoIdMongoCollection[ItemType](implicit itemTypeTag: ClassTag[ItemType]) extends MongoCollection[ItemType, Int] with MongoCounter {
  def counterName: String = collectionName
}

object MongoCollection {

  implicit class ConvertableCursor[T](cursor: DBCursor[T]) {
    def toSeq: Seq[T] = {
      try {
        cursor.iterator.asScala.toList
      } finally {
        cursor.close()
      }
    }
  }

}