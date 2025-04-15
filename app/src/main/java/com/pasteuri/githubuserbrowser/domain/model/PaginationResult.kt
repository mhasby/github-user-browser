package com.pasteuri.githubuserbrowser.domain.model

data class PaginationResult<out S : Any>(
    val total: Int,
    val nextPage: Int?,
    val items: List<S>
)
