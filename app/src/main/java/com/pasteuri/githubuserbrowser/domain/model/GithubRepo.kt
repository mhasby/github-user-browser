package com.pasteuri.githubuserbrowser.domain.model

data class GithubRepo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String,
    val url: String,
    val language: String,
    val stars: Int
)
