package com.example.stackexchange.usercore.model

data class TopTagsListResponse(
    val items: List<TopTagResponse>
)

data class TopTagResponse(
    val tag_name: String,
)