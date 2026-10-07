package com.example.stackexchange.usercore

import com.example.stackexchange.usercore.model.Badge
import com.example.stackexchange.network.BadgeResponse
import com.example.stackexchange.usercore.model.TopTag
import com.example.stackexchange.network.TopTagResponse
import com.example.stackexchange.usercore.model.User
import com.example.stackexchange.network.UserResponse
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

fun TopTagResponse.toTopTag(): TopTag =
    TopTag(
        name = tag_name
    )

fun BadgeResponse.toBadge(): Badge =
    Badge(
        id = badge_id,
        name = name,
        rank = rank
    )