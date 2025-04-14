package com.pasteuri.githubuserbrowser.domain.repository

import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GithubRepoRepository {
    fun getRepoByUser(
        username: String,
        type: User.Type,
        perPage: Int?,
        page: Int?,
        filterType: ListFilterType?,
        sort: ListSort?,
        order: ListOrder?
    ): Flow<Result<List<GithubRepo>>>

    enum class ListFilterType {
        ALL, PUBLIC, PRIVATE, FORKS, SOURCES, MEMBER, OWNER
    }
    enum class ListSort {
        CREATED, UPDATED, PUSHED, FULL_NAME
    }
    enum class ListOrder {
        ASC, DESC
    }
}
