package com.samebug.crashtest.services

import javax.inject.Inject

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

class MysqlCrasher @Inject()(db: Database) extends Reporter {
  def connect() = {
    try {
      val q = sqlu"""select * from test"""
      Await.result(db.run(q), Duration.Inf)
    } catch {
      case NonFatal(x) => report(x)
    }
  }
}
