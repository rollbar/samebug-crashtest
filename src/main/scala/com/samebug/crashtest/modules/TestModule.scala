package com.samebug.crashtest.modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class TestModule(mysqlConfig: MysqlModuleConfig, mongoConfig: MongoModuleConfig) extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    install(new MongoModule(mongoConfig))
    install(new MysqlModule(mysqlConfig))
  }
}
