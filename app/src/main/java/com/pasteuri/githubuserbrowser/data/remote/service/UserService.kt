package com.pasteuri.githubuserbrowser.data.remote.service

import com.pasteuri.githubuserbrowser.data.remote.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {

    @GET("user/{id}")
    suspend fun getUser(
        @Path("id") id: Long
    ): UserResponse
}
