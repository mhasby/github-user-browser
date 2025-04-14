package com.pasteuri.githubuserbrowser.domain.repository

import com.pasteuri.githubuserbrowser.domain.model.User

interface UserRepository {
    suspend fun searchUsers(
        query: String,
        perPage: Int?,
        page: Int?,
        sort: SearchUserSort?,
        order: SearchOrder?
    ): Result<List<User>>

    suspend fun getUserDetail(id: Long): Result<User>

    enum class SearchUserSort {
        FOLLOWERS, REPOSITORIES, JOINED
    }
    enum class SearchOrder {
        ASC, DESC
    }
}