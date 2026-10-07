package com.example.stackexchange.network

data class TopTagsListResponse(
    val items: List<TopTagResponse>
)

data class TopTagResponse(
    val tag_name: String,
)