package com.samebug.entities

trait User {
  def id: Long
  def slug: String
  def displayName: String
  def avatarUrl: String
  def defaultWorkspaceId: Long
}

case class UserReference(
  id: Long,
  slug: String,
  displayName: String,
  avatarUrl: String,
  defaultWorkspaceId: Long
) extends User
