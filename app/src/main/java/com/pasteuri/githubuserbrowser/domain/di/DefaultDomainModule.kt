package com.pasteuri.githubuserbrowser.domain.di

import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import com.pasteuri.githubuserbrowser.domain.usecase.CacheVisitedUserUseCase
import com.pasteuri.githubuserbrowser.domain.usecase.GetCachedVisitedUsersUseCase
import com.pasteuri.githubuserbrowser.domain.usecase.GetGithubRepoByUserUseCase
import com.pasteuri.githubuserbrowser.domain.usecase.GetUserDetailUseCase
import com.pasteuri.githubuserbrowser.domain.usecase.SearchUsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class) // TODO: change it view model scope
object DefaultDomainModule {

    @Provides
    fun provideSearchUsersUseCase(userRepository: UserRepository): SearchUsersUseCase =
        SearchUsersUseCase(userRepository)

    @Provides
    fun provideGetUserDetailUseCase(
        userRepository: UserRepository
    ): GetUserDetailUseCase = GetUserDetailUseCase(userRepository)

    @Provides
    fun provideGetGithubRepoByUserUseCase(
        githubRepoRepository: GithubRepoRepository
    ): GetGithubRepoByUserUseCase = GetGithubRepoByUserUseCase(githubRepoRepository)

    @Provides
    fun provideGetCachedVisitedUsersUseCase(
        userRepository: UserRepository
    ): GetCachedVisitedUsersUseCase = GetCachedVisitedUsersUseCase(userRepository)

    @Provides
    fun provideCacheVisitedUserUseCase(
        userRepository: UserRepository
    ): CacheVisitedUserUseCase = CacheVisitedUserUseCase(userRepository)
}
