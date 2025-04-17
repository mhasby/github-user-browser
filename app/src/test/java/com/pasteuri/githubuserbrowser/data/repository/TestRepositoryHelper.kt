package com.pasteuri.githubuserbrowser.data.repository

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TestRepositoryHelper {

    val retrofitBuilder: (MockWebServer) -> Retrofit = {
        Retrofit.Builder()
            .baseUrl(it.url("/"))
            .client(
                OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}