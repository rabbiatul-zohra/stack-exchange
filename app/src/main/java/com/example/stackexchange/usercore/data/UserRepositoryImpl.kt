package com.example.stackexchange.usercore.data

import android.net.http.HttpException
import com.example.stackexchange.usercore.model.User
import com.example.stackexchange.usercore.toUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

interface UserRepository {
    fun getUsers(query: String) : Flow<List<User>>
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
}