package com.pasteuri.githubuserbrowser.data.remote.repository

import android.util.Log
import com.pasteuri.githubuserbrowser.data.remote.model.toDomain
import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository

class DefaultGithubRepoRepository(
    githubRepoService: () -> GithubRepoService
) : GithubRepoRepository {
    private val service by lazy(githubRepoService)

    override suspend fun getRepoByUser(
        user: User,
        perPage: Int?,
        page: Int?,
        filterType: GithubRepoRepository.ListFilterType?,
        sort: GithubRepoRepository.ListSort?,
        order: GithubRepoRepository.ListOrder?
    ): Result<List<GithubRepo>> {
        return try {
            val filterTypeQuery = when(filterType) {
                null -> null
                else -> filterType.name.lowercase()
            }
            val sortQuery = when(sort) {
                null -> null
                else -> sort.name.lowercase()
            }
            val orderQuery = when(order) {
                null -> null
                else -> order.name.lowercase()
            }
            val result = when (user.type) {
                User.Type.USER -> service.getUserRepositories(user.username, perPage, page, filterTypeQuery, sortQuery, orderQuery)
                User.Type.ORG -> service.getOrgRepositories(user.username, perPage, page, filterTypeQuery, sortQuery, orderQuery)
            }
            Log.d("TESTT", "result get repo success size = ${result.size}")
            Result.success(result.map { it.toDomain() })
        } catch (e: Exception) {
            Log.d("TESTT", "result get repo error : $e")
            Result.failure(e)
        }
    }
}
