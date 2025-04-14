package com.pasteuri.githubuserbrowser.data.remote.service

import com.pasteuri.githubuserbrowser.data.remote.model.GithubRepoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubRepoService {

    @GET("orgs/{org_name}/repos")
    suspend fun getOrgRepositories(
        @Path("org_name") orgName: String,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query("type") type: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null
    ): List<GithubRepoResponse>

    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query("type") type: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null
    ): List<GithubRepoResponse>
}
