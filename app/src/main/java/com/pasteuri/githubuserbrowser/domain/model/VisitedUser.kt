package com.pasteuri.githubuserbrowser.domain.model

import com.pasteuri.githubuserbrowser.domain.model.User.Type
import kotlinx.serialization.Serializable

@Serializable
data class VisitedUser(
    val username: String,
    val type: Type,
    val avatarUrl: String,
)
