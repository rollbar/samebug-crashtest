package com.samebug.entities

case class User(
  id: Long,
  slug: String,
  displayName: String,
  avatarUrl: String,
  defaultWorkspaceId: Long
)
