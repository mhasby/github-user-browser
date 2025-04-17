package com.pasteuri.githubuserbrowser.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CachedOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitWithCachedOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultRetrofit