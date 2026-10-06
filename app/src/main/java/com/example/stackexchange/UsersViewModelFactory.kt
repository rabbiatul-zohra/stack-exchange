package com.example.stackexchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stackexchange.user.presentation.UsersViewModel
import com.example.stackexchange.usercore.data.UserRepository
import kotlin.jvm.java

class UsersViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(repository) as T
        }

        throw kotlin.IllegalArgumentException("Unknown ViewModel class")
    }
}