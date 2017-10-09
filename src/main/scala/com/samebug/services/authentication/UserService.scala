package com.samebug.services.authentication

import com.samebug.entities.User

trait UserService {
  final def user(userId: Long): User = getUser(userId) getOrElse {throw NoSuchUserId(userId)}
  def getUser(userId: Long): Option[User]

}

case class NoSuchUserId(userId: Long) extends scala.Exception(s"There is no user with the id $userId")
