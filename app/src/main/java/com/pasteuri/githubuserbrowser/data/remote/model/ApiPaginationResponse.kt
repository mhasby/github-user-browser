package com.pasteuri.githubuserbrowser.data.remote.model

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult

data class ApiPaginationResponse<out S : Any>(
    @SerializedName("total_count")
    val totalCount: Int? = null,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean? = null,
    @SerializedName("items")
    val items: List<S>? = null
)

fun <S : Any, D : Any> ApiPaginationResponse<S>.toDomain(
    headerLink: String?,
    itemMapper: (S) -> D
) = PaginationResult(
    total = totalCount ?: 0,
    nextPage = parseNextPage(headerLink),
    items = items?.map { itemMapper(it) }.orEmpty()
)

private fun parseNextPage(linkHeader: String?): Int? {
    if (linkHeader == null) return null

    val links = linkHeader.split(",")
    for (link in links) {
        val parts = link.split(";")
        if (parts.size < 2) continue
        val urlPart = parts[0].trim().removePrefix("<").removeSuffix(">")
        val relPart = parts[1].trim()

        if (relPart == "rel=\"next\"") {
            val uri = Uri.parse(urlPart)
            return uri.getQueryParameter("page")?.toIntOrNull()
        }
    }
    return null
}
