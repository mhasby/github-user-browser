package com.pasteuri.githubuserbrowser.data.remote.model

import com.google.gson.annotations.SerializedName

class ApiPaginationResponse<out S : Any> {
    @SerializedName("total_count")
    val totalCount: Int? = null

    @SerializedName("incomplete_results")
    val incompleteResult: Boolean? = null

    @SerializedName("items")
    val items: List<S>? = null
}