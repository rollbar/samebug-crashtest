package com.samebug.crashtest.services

import scala.util.control.NonFatal

object RegexCrasher extends Reporter {
  def badGroupIndex() = {
    try {
      "hello world".replaceAll("hello", "$q")
    } catch {
      case NonFatal(x) => report(x)
    }
  }
}
