package com.pasteuri.githubuserbrowser.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.pasteuri.githubuserbrowser.data.remote.RetrofitBuilder
import com.pasteuri.githubuserbrowser.data.repository.DefaultGithubRepoRepository
import com.pasteuri.githubuserbrowser.data.repository.DefaultUserRepository
import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("app_data_store")
        }
    }

    @Provides
    fun provideUserRepository(
        userService: Provider<UserService>,
        searchService: Provider<SearchService>,
        dataStore: DataStore<Preferences>
    ): UserRepository = DefaultUserRepository(userService::get, searchService::get, dataStore)

    @Provides
    fun provideGithubRepoRepository(
        githubRepoService: Provider<GithubRepoService>
    ): GithubRepoRepository = DefaultGithubRepoRepository(githubRepoService::get)
}