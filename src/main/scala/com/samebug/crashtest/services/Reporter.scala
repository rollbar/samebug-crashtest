package com.samebug.crashtest.services

trait Reporter {
  def report(x: Throwable) = {
    x.printStackTrace()
  }
}
