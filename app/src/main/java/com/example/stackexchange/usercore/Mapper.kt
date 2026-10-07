package com.example.stackexchange.usercore

import com.example.stackexchange.usercore.model.Badge
import com.example.stackexchange.network.BadgeResponse
import com.example.stackexchange.usercore.model.TopTag
import com.example.stackexchange.network.TopTagResponse
import com.example.stackexchange.usercore.model.User
import com.example.stackexchange.network.UserResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun UserResponse.toUser() = User(
    id = user_id,
    name = display_name,
    reputation = reputation,
    profileImageUrl = profile_image,
    location = location,
    creationDate = creation_date.toFormattedDate()
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


fun Long.toFormattedDate(): String {
    val date = Date(this * 1000L)

    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    return formatter.format(date)
}