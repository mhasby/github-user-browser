package com.pasteuri.githubuserbrowser.domain.repository

import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
import com.pasteuri.githubuserbrowser.domain.model.User

interface GithubRepoRepository {
    suspend fun getRepoByUser(
        username: String,
        type: User.Type,
        perPage: Int?,
        page: Int?,
        filterType: ListFilterType?,
        sort: ListSort?,
        order: ListOrder?
    ): Result<PaginationResult<GithubRepo>>

    enum class ListFilterType {
        ALL, PUBLIC, FORKS, SOURCES, MEMBER, OWNER
    }
    enum class ListSort {
        PUSHED, CREATED, UPDATED, FULL_NAME
    }
    enum class ListOrder {
        DESC, ASC
    }
}
