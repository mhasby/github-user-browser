package com.pasteuri.githubuserbrowser.domain.model

data class User(
    val id: Long,
    val username: String,
    val type: Type,
    val name: String,
    val followers: Int,
    val avatarUrl: String,
    val following: Int,
    val publicRepoCount: Int,
    val publicGistCount: Int
) {
    enum class Type {
        USER, ORG
    }
}
