package com.pasteuri.githubuserbrowser.data.remote.repository

import com.pasteuri.githubuserbrowser.data.remote.model.parseNextPage
import com.pasteuri.githubuserbrowser.data.remote.model.toDomain
import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository

class DefaultGithubRepoRepository(
    githubRepoService: () -> GithubRepoService
) : GithubRepoRepository {
    private val service by lazy(githubRepoService)

    override suspend fun getRepoByUser(
        username: String,
        type: User.Type,
        perPage: Int?,
        page: Int?,
        filterType: GithubRepoRepository.ListFilterType?,
        sort: GithubRepoRepository.ListSort?,
        order: GithubRepoRepository.ListOrder?
    ): Result<PaginationResult<GithubRepo>> {
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
            val result = when (type) {
                User.Type.USER -> service.getUserRepositories(username, perPage, page, filterTypeQuery, sortQuery, orderQuery)
                User.Type.ORG -> service.getOrgRepositories(username, perPage, page, filterTypeQuery, sortQuery, orderQuery)
            }
            val resultBody = result.body() ?: return Result.failure(Exception())
            Result.success(
                PaginationResult(
                    total = 0,
                    nextPage = result.headers().parseNextPage(),
                    items = resultBody.map { it.toDomain() }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
