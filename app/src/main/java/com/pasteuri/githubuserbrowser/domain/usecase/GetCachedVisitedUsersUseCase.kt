package com.pasteuri.githubuserbrowser.domain.usecase

import com.pasteuri.githubuserbrowser.domain.model.VisitedUser
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import com.pasteuri.githubuserbrowser.domain.usecase.CacheVisitedUserUseCase.Companion.CACHED_VISITED_USER_LIMIT
import kotlinx.coroutines.flow.Flow

class GetCachedVisitedUsersUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(): Flow<List<VisitedUser>> {
        return userRepository.getCachedVisitedUsers(CACHED_VISITED_USER_LIMIT)
    }
}
