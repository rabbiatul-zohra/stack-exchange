package com.example.stackexchange.usercore

import com.example.stackexchange.usercore.model.User
import com.example.stackexchange.usercore.model.UserResponse
import java.time.Instant
import java.time.ZoneId

fun UserResponse.toUser() = User(
    id = user_id,
    name = display_name,
    reputation = reputation,
    profileImageUrl = profile_image,
    location = location,
    creationDate = Instant
        .ofEpochSecond(creation_date)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
)
