package com.pasteuri.githubuserbrowser.domain.usecase

import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SearchUsersUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(
        query: String,
        searchUserSort: UserRepository.SearchUserSort? = null,
        searchOrder: UserRepository.SearchOrder? = null
    ): Flow<Result<List<User>>> {
        return userRepository.searchUsers(query, SEARCH_PER_PAGE, 1, searchUserSort, searchOrder)
    }

    companion object {
        private const val SEARCH_PER_PAGE = 50
    }
}
