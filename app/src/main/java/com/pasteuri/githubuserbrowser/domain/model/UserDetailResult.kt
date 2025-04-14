package com.pasteuri.githubuserbrowser.domain.model

sealed class UserDetailResult {
    data class UserLoaded(val user: User) : UserDetailResult()
    data class UserError(val throwable: Throwable) : UserDetailResult()
    data class RepositoriesLoaded(val repos: List<GithubRepo>) : UserDetailResult()
    data class RepositoriesError(val throwable: Throwable) : UserDetailResult()
}
