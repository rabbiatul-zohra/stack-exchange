package com.example.stackexchange.network

data class BadgesListResponse(
    val items: List<BadgeResponse>
)

data class BadgeResponse(
    val badge_id: Int,
    val name: String,
    val rank: String
)