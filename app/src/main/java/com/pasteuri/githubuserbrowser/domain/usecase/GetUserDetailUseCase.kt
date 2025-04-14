package com.pasteuri.githubuserbrowser.domain.usecase

import com.pasteuri.githubuserbrowser.domain.model.User
import com.pasteuri.githubuserbrowser.domain.model.UserDetailResult
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class GetUserDetailUseCase(
    private val userRepository: UserRepository,
    private val githubRepoRepository: GithubRepoRepository
) {

    operator fun invoke(username: String, type: User.Type): Flow<UserDetailResult> = channelFlow {
        launch {
            val userResult = userRepository.getUserDetail(username)
            userResult
                .onSuccess {
                    send(UserDetailResult.UserLoaded(it))
                }.onFailure {
                    send(UserDetailResult.UserError(it))
                }
        }

        launch {
            githubRepoRepository
                .getRepoByUser(username, type, null, null, null, null, null)
                .collect { result ->
                    result
                        .onSuccess {
                            send(UserDetailResult.RepositoriesLoaded(it))
                        }
                        .onFailure {
                            send(UserDetailResult.RepositoriesError(it))
                        }
                }
        }
    }
}
