package com.pasteuri.githubuserbrowser.data.remote.repository

import android.util.Log
import com.pasteuri.githubuserbrowser.data.remote.model.toDomain
import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultGithubRepoRepository(
    githubRepoService: () -> GithubRepoService
) : GithubRepoRepository {
    private val service by lazy(githubRepoService)

    // TODO: change it to use jetpack pagination
    override fun getRepoByUser(
        username: String,
        type: User.Type,
        perPage: Int?,
        page: Int?,
        filterType: GithubRepoRepository.ListFilterType?,
        sort: GithubRepoRepository.ListSort?,
        order: GithubRepoRepository.ListOrder?
    ): Flow<Result<List<GithubRepo>>> = flow {
        try {
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
            val result = when (type) {
                User.Type.USER -> service.getUserRepositories(username, perPage, page, filterTypeQuery, sortQuery, orderQuery)
                User.Type.ORG -> service.getOrgRepositories(username, perPage, page, filterTypeQuery, sortQuery, orderQuery)
            }
            Log.d("TESTT", "result get repo success size = ${result.size}")
            emit(Result.success(result.map { it.toDomain() }))
        } catch (e: Exception) {
            Log.d("TESTT", "result get repo error : $e")
            emit(Result.failure(e))
        }
    }
}
