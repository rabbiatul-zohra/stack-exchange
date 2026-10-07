package com.example.stackexchange.network

data class SearchUserListResponse(
    val items: List<UserResponse>
)
data class UserResponse(
    val user_id: Int,
    val display_name: String,
    val reputation: Int,
    val profile_image: String,
    val location: String?,
    val creation_date: Long
)
