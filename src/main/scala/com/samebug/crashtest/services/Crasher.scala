package com.samebug.crashtest.services

import scala.util.control.NonFatal

class Crasher extends Reporter {
  def failsToInit(): Unit = {
    try {
      println(new FailsToInit)
    } catch {
      case NonFatal(x) => report(x)
    }
  }

  def failsToClInit(): Unit = {
    try {
      println(FailsToClInit)
    } catch {
      case NonFatal(x) => report(x)
    }
  }

  def chainedException(nFrames: List[Int]): Unit = {
    try {
      println(_chainedException(nFrames))
    } catch {
      case NonFatal(x) => report(x)
    }
  }

  private def _chainedException(nFrames: List[Int]): Unit = {
    require(nFrames.nonEmpty)
    require(nFrames.head > 0)

    nFrames match {
      case 1 :: Nil => throw TestException(s"Bang at depth ${nFrames.size - 1}")
      case 1 :: s =>
        try {
          _chainedException(s)
        } catch {
          case NonFatal(x) => throw TestException(s"Bang at depth ${nFrames.size - 1}", x)
        }
      case i :: s => _chainedException((i - 1) :: s)
      case _ => throw new IllegalStateException
    }
    // make sure it is not tail recursive
    Unit
  }
}

private case class TestException(msg: String, cause: Throwable = null) extends scala.Exception(msg, cause)

private class FailsToInit {
  val x: String = null
  x.toString
}

private object FailsToClInit {
  val x: String = null
  x.toString
}
