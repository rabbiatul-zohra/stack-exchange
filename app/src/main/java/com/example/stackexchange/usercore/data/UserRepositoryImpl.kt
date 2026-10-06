package com.example.stackexchange.usercore.data

import android.net.http.HttpException
import com.example.stackexchange.usercore.model.User
import com.example.stackexchange.usercore.toUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

interface UserRepository {
    fun getUsers(query: String) : Flow<List<User>>
    fun getUserById(id: Int): Flow<User>
}
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
}