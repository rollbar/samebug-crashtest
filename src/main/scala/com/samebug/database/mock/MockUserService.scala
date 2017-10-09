package com.samebug.database.mock

import com.samebug.entities.{User, UserReference}
import com.samebug.services.authentication.UserService

class MockUserService extends UserService {
  override def getUser(userId: Long) = {
    val mockUser = UserReference(userId, "poroszd", "poroszd", "https://samebug.io/avatars/1/3", 2848)
    Some(mockUser)
  }
}
