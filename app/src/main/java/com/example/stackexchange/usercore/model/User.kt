package com.example.stackexchange.usercore.model

import java.time.LocalDate

data class UserList(
    val users: List<User>
)
data class User(
    val id: Int,
    val name: String,
    val reputation: Int,
    val profileImageUrl: String,
    val location: String?,
    val creationDate: LocalDate
)
