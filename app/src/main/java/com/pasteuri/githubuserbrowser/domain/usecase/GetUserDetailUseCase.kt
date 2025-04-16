package com.pasteuri.githubuserbrowser.domain.usecase

import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository

class GetUserDetailUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(username: String): Result<User> {
        return userRepository.getUserDetail(username)
    }
}
