package com.pasteuri.githubuserbrowser.domain.usecase

import com.pasteuri.githubuserbrowser.domain.model.VisitedUser
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository

class CacheVisitedUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(visitedUser: VisitedUser) {
        userRepository.cacheVisitedUser(visitedUser, CACHED_VISITED_USER_LIMIT)
    }

    companion object {
        const val CACHED_VISITED_USER_LIMIT = 5
    }
}