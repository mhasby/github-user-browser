package com.pasteuri.githubuserbrowser.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SearchUsersUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(
        query: String,
        searchUserSort: UserRepository.SearchUserSort? = null,
        searchOrder: UserRepository.SearchOrder? = null
    ): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = SEARCH_PER_PAGE, prefetchDistance = 2),
            pagingSourceFactory = {
                SearchUsersPagingSource {
                    userRepository.searchUsers(
                        query = query,
                        perPage = SEARCH_PER_PAGE,
                        page = it,
                        sort = searchUserSort,
                        order = searchOrder
                    )
                }
            }
        ).flow
    }

    companion object {
        private const val SEARCH_PER_PAGE = 20
    }
}

class SearchUsersPagingSource(
    private val searchUser: suspend (Int) -> Result<PaginationResult<User>>,
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val currentPage = params.key ?: 1
        val result = searchUser(currentPage)
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

    override fun getRefreshKey(state: PagingState<Int, User>): Int? = null
}
