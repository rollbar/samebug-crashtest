package com.samebug.sandbox.crashtest

import scala.util.control.NonFatal

class CrashReport {
  def failsToInit() = {
    println(new FailsToInit)
  }

  def failsToClInit() = {
    println(FailsToClInit)
  }

  def chainedException(nFrames: List[Int]): Unit = {
    require(nFrames.nonEmpty)
    require(nFrames.head > 0)

    nFrames match {
      case 1 :: Nil => throw TestException(s"Bang at depth ${nFrames.size - 1}")
      case 1 :: s =>
        try {
          chainedException(s)
        } catch {
          case NonFatal(x) => throw TestException(s"Bang at depth ${nFrames.size - 1}", x)
        }
      case i :: s => a(i :: s)
      case _ => throw new IllegalStateException
    }
    // make sure it is not tail recursive
    Unit
  }

  def a(nFrames: List[Int]): Unit = {
    b(nFrames)
  }

  def b(nFrames: List[Int]): Unit = nFrames match {
    case 1 :: s => chainedException(1 :: s)
    case i :: s => a(i - 1 :: s)
  }
}

case class TestException(msg: String, cause: Throwable = null) extends scala.Exception(msg, cause)

class FailsToInit {
  val x: String = null
  x.toString
}

object FailsToClInit {
  val x: String = null
  x.toString
}