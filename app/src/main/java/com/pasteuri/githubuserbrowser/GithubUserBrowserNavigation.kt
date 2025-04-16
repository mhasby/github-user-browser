package com.pasteuri.githubuserbrowser

import android.net.Uri
import com.google.gson.Gson
import com.pasteuri.githubuserbrowser.domain.model.User

sealed class GithubUserBrowserNavigation(val routeName: String) {
    data object HomeRoute: GithubUserBrowserNavigation("home")
    data object DetailRoute: GithubUserBrowserNavigation("detail/{user}") {
        fun createRoute(user: UserDetailArg) = "detail/${Uri.encode(Gson().toJson(user))}"
    }
    data object GithubRepoRoute: GithubUserBrowserNavigation("repo/{title}/{url}") {
        fun createRoute(title: String, url: String) = "repo/$title/${Uri.encode(url)}"
    }
}

data class UserDetailArg(
    val username: String,
    val type: User.Type
)
