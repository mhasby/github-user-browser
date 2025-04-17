package com.pasteuri.githubuserbrowser.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pasteuri.githubuserbrowser.data.remote.model.toDomain
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.model.VisitedUser
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchOrder
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository.SearchUserSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DefaultUserRepository(
    userService: () -> UserService,
    searchService: () -> SearchService,
    private val dataStore: DataStore<Preferences>
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

    override suspend fun cacheVisitedUser(visitedUser: VisitedUser, limit: Int) {
        dataStore.updateData { prefs ->
            val currentList = getCachedUsers(prefs)
            val newList = (listOf(visitedUser) + currentList.filter { it.username != visitedUser.username })
                .take(limit)

            prefs.toMutablePreferences().apply {
                this[VISITED_USERS_KEY] = json.encodeToString(newList)
            }
        }
    }

    override fun getCachedVisitedUsers(limit: Int): Flow<List<VisitedUser>> {
        return dataStore.data.map { prefs ->
            getCachedUsers(prefs).take(limit)
        }
    }

    private fun getCachedUsers(prefs: Preferences): List<VisitedUser> {
        val jsonString = prefs[VISITED_USERS_KEY]
        return jsonString?.let {
            runCatching {
                json.decodeFromString<List<VisitedUser>>(it)
            }.getOrDefault(emptyList())
        } ?: emptyList()
    }

    companion object {
        private val VISITED_USERS_KEY = stringPreferencesKey("visited_users")
        private val json = Json { ignoreUnknownKeys = true }
    }
}
