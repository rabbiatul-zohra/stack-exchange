package com.example.stackexchange.usercore.data

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.stackexchange.network.UserService
import com.example.stackexchange.usercore.model.Badge
import com.example.stackexchange.usercore.model.TopTag
import com.example.stackexchange.usercore.model.User
import com.example.stackexchange.usercore.toBadge
import com.example.stackexchange.usercore.toTopTag
import com.example.stackexchange.usercore.toUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

interface UserRepository {
    fun getUsers(query: String) : Flow<List<User>>
    fun getUserById(id: Int): Flow<User>
    fun getUserTopTags(id: Int): Flow<List<TopTag>>
    fun getUserBadges(id: Int): Flow<List<Badge>>

}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
class UserRepositoryImpl(
    private val service: UserService,
) : UserRepository {
    override fun getUsers(query: String): Flow<List<User>> = flow {
        try {
            val response = service.getUsers(query)

            val users = response.items.map { userResponse ->
                userResponse.toUser()
            }

            emit(users)

        } catch (exception: HttpException) {
            throw exception

        } catch (exception: IOException) {
            throw exception
        }
    }

    override fun getUserById(id: Int): Flow<User> = flow {
        try {
            val response = service.getUserById(id)
            val user = response.items.firstOrNull()
                ?: throw IllegalStateException("User not found")
            emit(user.toUser())
        } catch (exception: HttpException) {
            throw exception

        } catch (exception: IOException) {
            throw exception
        }
    }

    override fun getUserTopTags(id: Int): Flow<List<TopTag>> = flow {
        try {
            val response = service.getUserTopTags(id)
            val topTags = response.items.map {
                it.toTopTag()
            }
            emit(topTags)
        } catch (exception: HttpException) {
            throw exception

        } catch (exception: IOException) {
            throw exception
        }
    }

    override fun getUserBadges(id: Int): Flow<List<Badge>> = flow {
        try {
            val response = service.getUserBadges(id)
            val badges = response.items.map { badgeResponse ->
                badgeResponse.toBadge()
            }

            emit(badges)
        } catch (exception: HttpException) {
            throw exception

        } catch (exception: IOException) {
            throw exception
        }
    }
}