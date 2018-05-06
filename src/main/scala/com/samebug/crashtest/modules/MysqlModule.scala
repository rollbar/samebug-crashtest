package com.samebug.crashtest.modules

import com.google.inject.AbstractModule
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.ScalaModule
import slick.jdbc.MySQLProfile.api.Database

class MysqlModule(config: MysqlModuleConfig) extends AbstractModule with ScalaModule with LazyLogging {
  override def configure(): Unit = {
    bind[Database].toInstance(database)
  }

  private lazy val jdbcUrl = s"jdbc:mariadb://${config.host}:${config.port}"
  private lazy val database = Database.forURL(s"$jdbcUrl/test")
}

case class MysqlModuleConfig(
  host: String,
  port: Int
)
