package com.example.stackexchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stackexchange.user.presentation.UserDetailViewModel
import com.example.stackexchange.user.presentation.UsersViewModel
import com.example.stackexchange.usercore.data.UserRepository
import kotlin.jvm.java

class ViewModelFactory(
    private val repository: UserRepository,
    private val userId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return when {

            modelClass.isAssignableFrom(UsersViewModel::class.java) -> {
                UsersViewModel(
                    repository = repository
                ) as T
            }

            modelClass.isAssignableFrom(UserDetailViewModel::class.java) -> {
                UserDetailViewModel(
                    userId = requireNotNull(userId) {
                        "userId is required for UserDetailViewModel"
                    },
                    repository = repository
                ) as T
            }

            else -> {
                throw IllegalArgumentException(
                    "Unknown ViewModel class: ${modelClass.name}"
                )
            }
        }
    }
}