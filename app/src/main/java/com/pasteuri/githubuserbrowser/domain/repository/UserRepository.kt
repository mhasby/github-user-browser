package com.pasteuri.githubuserbrowser.domain.repository

import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.model.VisitedUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun searchUsers(
        query: String,
        perPage: Int?,
        page: Int?,
        sort: SearchUserSort?,
        order: SearchOrder?
    ): Result<PaginationResult<User>>

    suspend fun getUserDetail(username: String): Result<User>

    suspend fun cacheVisitedUser(visitedUser: VisitedUser, limit: Int)

    fun getCachedVisitedUsers(limit: Int): Flow<List<VisitedUser>>

    enum class SearchUserSort {
        FOLLOWERS, REPOSITORIES, JOINED
    }
    enum class SearchOrder {
        DESC, ASC
    }
}