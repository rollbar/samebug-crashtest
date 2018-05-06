package com.samebug.crashtest.services
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class RegexCrasherTest extends FunSpec with BeforeAndAfterAll {
  it("crashes with bad group backreference") {
    sut.badGroupIndex()
  }

  val sut = RegexCrasher
}
