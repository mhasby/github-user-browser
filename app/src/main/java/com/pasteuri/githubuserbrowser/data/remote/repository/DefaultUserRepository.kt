package com.pasteuri.githubuserbrowser.data.remote.repository

import com.pasteuri.githubuserbrowser.data.remote.model.toDomain
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
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
    ): Result<PaginationResult<User>> {
        if (query.isBlank()) return Result.success(PaginationResult(0, null, emptyList()))
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
            val resultBody = result.body() ?: return Result.failure(Exception())
            Result.success(
                resultBody.toDomain(result.headers()) { item ->
                    item.toDomain()
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserDetail(username: String): Result<User> {
        return try {
            val result = userService.getUser(username)
            Result.success(result.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
