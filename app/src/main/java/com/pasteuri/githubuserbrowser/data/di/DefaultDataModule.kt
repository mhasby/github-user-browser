package com.pasteuri.githubuserbrowser.data.di

import com.pasteuri.githubuserbrowser.data.remote.RetrofitBuilder
import com.pasteuri.githubuserbrowser.data.remote.repository.DefaultGithubRepoRepository
import com.pasteuri.githubuserbrowser.data.remote.repository.DefaultUserRepository
import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DefaultDataModule {

    @Provides
    @Singleton
    fun provideSearchService(): SearchService = RetrofitBuilder.createService(SearchService::class.java)

    @Provides
    @Singleton
    fun provideUserService(): UserService = RetrofitBuilder.createService(UserService::class.java)

    @Provides
    @Singleton
    fun provideGithubRepoService(): GithubRepoService = RetrofitBuilder.createService(GithubRepoService::class.java)

    @Provides
    fun provideUserRepository(
        userService: Provider<UserService>,
        searchService: Provider<SearchService>
    ): UserRepository = DefaultUserRepository(userService::get, searchService::get)

    @Provides
    fun provideGithubRepoRepository(
        githubRepoService: Provider<GithubRepoService>
    ): GithubRepoRepository = DefaultGithubRepoRepository(githubRepoService::get)
}