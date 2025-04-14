package com.pasteuri.githubuserbrowser.domain.repository

import com.pasteuri.githubuserbrowser.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun searchUsers(
        query: String,
        perPage: Int?,
        page: Int?,
        sort: SearchUserSort?,
        order: SearchOrder?
    ): Flow<Result<List<User>>>

    suspend fun getUserDetail(username: String): Result<User>

    enum class SearchUserSort {
        FOLLOWERS, REPOSITORIES, JOINED
    }
    enum class SearchOrder {
        ASC, DESC
    }
}