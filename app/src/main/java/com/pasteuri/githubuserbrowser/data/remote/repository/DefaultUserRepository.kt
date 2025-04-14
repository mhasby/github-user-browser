package com.pasteuri.githubuserbrowser.data.remote.repository

import android.util.Log
import com.pasteuri.githubuserbrowser.data.remote.model.toDomain
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchOrder
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchUserSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultUserRepository(
    userService: () -> UserService,
    searchService: () -> SearchService
) : UserRepository {
    private val userService by lazy(userService)
    private val searchService by lazy(searchService)

    // TODO: change it to use jetpack pagination
    override fun searchUsers(
        query: String,
        perPage: Int?,
        page: Int?,
        sort: SearchUserSort?,
        order: SearchOrder?
    ): Flow<Result<List<User>>> = flow {
        try {
            val sortQuery = when(sort) {
                null -> null
                else -> sort.name.lowercase()
            }
            val orderQuery = when(order) {
                null -> null
                else -> order.name.lowercase()
            }
            val result = searchService.searchUsers(query, perPage, page, sortQuery, orderQuery)
            Log.d("TESTT", "result search user success size = ${result.items?.size}")
            emit(Result.success(result.items?.map { it.toDomain() }.orEmpty()))
        } catch (e: Exception) {
            Log.d("TESTT", "result error : $e")
            emit(Result.failure(e))
        }
    }

    override suspend fun getUserDetail(username: String): Result<User> {
        return try {
            val result = userService.getUser(username)
            Log.d("TESTT", "result get detail success : $result")
            Result.success(result.toDomain())
        } catch (e: Exception) {
            Log.d("TESTT", "result get detail error : $e")
            Result.failure(e)
        }
    }
}
