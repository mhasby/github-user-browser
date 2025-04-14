package com.pasteuri.githubuserbrowser.data.remote.service

import com.pasteuri.githubuserbrowser.data.remote.model.ApiPaginationResponse
import com.pasteuri.githubuserbrowser.data.remote.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
    ): ApiPaginationResponse<UserResponse>
}
