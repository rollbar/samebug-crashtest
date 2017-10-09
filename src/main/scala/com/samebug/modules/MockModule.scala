package com.samebug.modules

import com.google.inject.AbstractModule
import com.samebug.database.mock.MockUserService
import com.samebug.services.authentication.UserService
import net.codingwell.scalaguice.ScalaModule

class MockModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    install(new MongoModule())
    bind[UserService].to[MockUserService]
  }
}
