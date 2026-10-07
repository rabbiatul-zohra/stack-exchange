package com.example.stackexchange.usercore.model

data class BadgesListResponse(
    val items: List<BadgeResponse>
)

data class BadgeResponse(
    val badge_id: Int,
    val name: String,
    val rank: String
)