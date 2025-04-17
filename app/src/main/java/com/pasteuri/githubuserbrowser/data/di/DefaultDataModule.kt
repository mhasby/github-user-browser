package com.pasteuri.githubuserbrowser.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.pasteuri.githubuserbrowser.data.remote.RetrofitBuilder
import com.pasteuri.githubuserbrowser.data.remote.service.GithubRepoService
import com.pasteuri.githubuserbrowser.data.remote.service.SearchService
import com.pasteuri.githubuserbrowser.data.remote.service.UserService
import com.pasteuri.githubuserbrowser.data.repository.DefaultGithubRepoRepository
import com.pasteuri.githubuserbrowser.data.repository.DefaultUserRepository
import com.pasteuri.githubuserbrowser.domain.repository.GithubRepoRepository
import com.pasteuri.githubuserbrowser.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DefaultDataModule {

    @CachedOkHttp
    @Singleton
    @Provides
    fun provideCachedOkHttp(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = 10L * 1024 * 1024 // 10 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)
        val builder = RetrofitBuilder.okHttpBuilder
            .cache(cache)
            .addNetworkInterceptor(RetrofitBuilder.cacheInterceptor)
        return builder.build()
    }

    @RetrofitWithCachedOkHttp
    @Singleton
    @Provides
    fun provideRetrofitWithCachedOkHttp(@CachedOkHttp okHttpClient: OkHttpClient): Retrofit {
        return RetrofitBuilder.retrofitInstance(okHttpClient)
    }

    @DefaultRetrofit
    @Singleton
    @Provides
    fun provideDefaultRetrofit(): Retrofit {
        return RetrofitBuilder.retrofitInstance(RetrofitBuilder.okHttpClient)
    }

    @Provides
    @Singleton
    fun provideSearchService(
        @RetrofitWithCachedOkHttp retrofit: Retrofit
    ): SearchService = retrofit.create(SearchService::class.java)

    @Provides
    @Singleton
    fun provideUserService(
        @DefaultRetrofit retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideGithubRepoService(
        @DefaultRetrofit retrofit: Retrofit
    ): GithubRepoService = retrofit.create(GithubRepoService::class.java)

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