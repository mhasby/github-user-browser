package com.pasteuri.githubuserbrowser.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasteuri.githubuserbrowser.domain.model.GithubRepo
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GetGithubRepoByUserUseCase(
    private val githubRepoRepository: GithubRepoRepository
) {

    operator fun invoke(
        username: String,
        type: User.Type,
    ): Flow<PagingData<GithubRepo>> {
        return Pager(
            config = PagingConfig(pageSize = SEARCH_PER_PAGE, prefetchDistance = 2),
            pagingSourceFactory = {
                RepositoriesPagingSource { page ->
                    githubRepoRepository.getRepoByUser(
                        username, type, null, page, null, null, null
                    )
                }
            }
        ).flow
    }

    companion object {
        private const val SEARCH_PER_PAGE = 20
    }
}

class RepositoriesPagingSource(
    private val listGithubRepo: suspend (Int) -> Result<PaginationResult<GithubRepo>>,
) : PagingSource<Int, GithubRepo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubRepo> {
        val currentPage = params.key ?: 1
        val result = listGithubRepo(currentPage)
        return suspendCoroutine { continuation ->
            result
                .onSuccess {
                    continuation.resume(
                        LoadResult.Page(
                            data = it.items,
                            prevKey = if (currentPage == 1) null else currentPage - 1,
                            nextKey = it.nextPage
                        )
                    )
                }
                .onFailure {
                    continuation.resume(
                        LoadResult.Error(it)
                    )
                }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GithubRepo>): Int? = null
}
