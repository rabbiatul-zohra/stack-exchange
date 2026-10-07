package com.example.stackexchange.usercore.model

data class User(
    val id: Int,
    val name: String,
    val reputation: Int,
    val profileImageUrl: String,
    val location: String?,
    val creationDate: String,
)
