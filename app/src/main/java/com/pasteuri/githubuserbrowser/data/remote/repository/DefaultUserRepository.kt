package com.pasteuri.githubuserbrowser.data.remote.repository

import android.util.Log
import com.pasteuri.githubuserbrowser.data.remote.model.toDomain
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchOrder
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchUserSort

class DefaultUserRepository(
    userService: () -> UserService,
    searchService: () -> SearchService
) : UserRepository {
    private val userService by lazy(userService)
    private val searchService by lazy(searchService)

    override suspend fun searchUsers(
        query: String,
        perPage: Int?,
        page: Int?,
        sort: SearchUserSort?,
        order: SearchOrder?
    ): Result<List<User>> {
        return try {
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
            Result.success(result.items?.map { it.toDomain() }.orEmpty())
        } catch (e: Exception) {
            Log.d("TESTT", "result error : $e")
            Result.failure(e)
        }
    }

    override suspend fun getUserDetail(id: Long): Result<User> {
        return try {
            val result = userService.getUser(id)
            Log.d("TESTT", "result get detail success : $result")
            Result.success(result.toDomain())
        } catch (e: Exception) {
            Log.d("TESTT", "result get detail error : $e")
            Result.failure(e)
        }
    }
}
