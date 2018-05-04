package com.samebug.crashtest.services

import com.typesafe.scalalogging.LazyLogging

trait Reporter extends LazyLogging {
  def report(x: Throwable) = {
    logger.error("", x)
  }
}
