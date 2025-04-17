package com.pasteuri.githubuserbrowser.data.remote.model

import com.google.gson.annotations.SerializedName
import com.pasteuri.githubuserbrowser.domain.model.PaginationResult
import okhttp3.Headers
import java.net.URL

data class ApiPaginationResponse<out S : Any>(
    @SerializedName("total_count")
    val totalCount: Int? = null,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean? = null,
    @SerializedName("items")
    val items: List<S>? = null
)

fun <S : Any, D : Any> ApiPaginationResponse<S>.toDomain(
    headers: Headers,
    itemMapper: (S) -> D
) = PaginationResult(
    total = totalCount ?: 0,
    nextPage = headers.parseNextPage(),
    items = items?.map { itemMapper(it) }.orEmpty()
)

fun Headers.parseNextPage(): Int? {
    val linkHeader = this["Link"] ?: return null
    val links = linkHeader.split(",")
    for (link in links) {
        val parts = link.split(";")
        if (parts.size < 2) continue
        val urlPart = parts[0].trim().removePrefix("<").removeSuffix(">")
        if (!parts[1].contains("next")) continue
        try {
            val url = URL(urlPart)
            val query = url.query
            query?.split("&")?.forEach { param ->
                if (param.startsWith("page=")) {
                    return param.removePrefix("page=").toIntOrNull()
                }
            }
        } catch (e: Exception) {
            return null
        }
    }
    return null
}
