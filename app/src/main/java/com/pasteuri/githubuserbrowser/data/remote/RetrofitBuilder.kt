package com.pasteuri.githubuserbrowser.data.remote

import com.pasteuri.githubuserbrowser.BuildConfig
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private const val BASE_URL ="https://api.github.com/"
    private const val CONNECT_TIMEOUT = 50L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    private val headerInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val requestBuilder = chain.request()
                .newBuilder()
                .addHeader("X-GitHub-Api-Version", "2022-11-28")
                .addHeader("Accept", "application/vnd.github.v3+json")
            if (BuildConfig.ACCESS_TOKEN.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer ${BuildConfig.ACCESS_TOKEN}")
            }
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    val cacheInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(3, TimeUnit.MINUTES)
                .build()
            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    val okHttpBuilder: () -> OkHttpClient.Builder = {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    }

    val okHttpClient: OkHttpClient by lazy {
        okHttpBuilder().build()
    }

    val retrofitInstance: (OkHttpClient) -> Retrofit = {
        Retrofit.Builder().baseUrl(BASE_URL)
            .client(it)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
