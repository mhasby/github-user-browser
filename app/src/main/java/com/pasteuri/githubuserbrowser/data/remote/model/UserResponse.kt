package com.pasteuri.githubuserbrowser.data.remote.model

import com.google.gson.annotations.SerializedName
import com.pasteuri.githubuserbrowser.domain.model.User

data class UserResponse(
    val login: String?,
    val id: Int?,
    @SerializedName("node_id")
    val nodeId: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("gravatar_id")
    val gravatarId: String?,
    val url: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("followers_url")
    val followersUrl: String?,
    @SerializedName("subscriptions_url")
    val subscriptionsUrl: String?,
    @SerializedName("organizations_url")
    val organizationsUrl: String?,
    @SerializedName("repos_url")
    val reposUrl: String?,
    @SerializedName("received_events_url")
    val receivedEventsUrl: String?,
    val type: String?,
    val score: Int?,
    @SerializedName("following_url")
    val followingUrl: String?,
    @SerializedName("gists_url")
    val gistsUrl: String?,
    @SerializedName("starred_url")
    val starredUrl: String?,
    @SerializedName("events_url")
    val eventsUrl: String?,
    @SerializedName("site_admin")
    val siteAdmin: Boolean?,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val hireable: Boolean? = null,
    val bio: String? = null,
    @SerializedName("twitter_username")
    val twitterUsername: String? = null,
    @SerializedName("public_repos")
    val publicRepos: Int?,
    @SerializedName("public_gists")
    val publicGists: Int?,
    val followers: Int?,
    val following: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

fun UserResponse.toDomain() = User(
    id = id?.toLong() ?: 0L,
    username = login.orEmpty(),
    avatarUrl = avatarUrl.orEmpty(),
    name = name.orEmpty(),
    followers = followers ?: 0,
    following = following ?: 0,
    publicRepoCount = publicRepos ?: 0,
    publicGistCount = publicGists ?: 0,
    type = when (type) {
        "User" -> User.Type.USER
        else -> User.Type.ORG
    }
)
