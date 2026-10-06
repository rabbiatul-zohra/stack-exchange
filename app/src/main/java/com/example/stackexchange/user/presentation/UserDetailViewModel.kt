package com.example.stackexchange.user.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackexchange.usercore.data.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class UserDetailUiModel(
    val name: String,
    val reputation: String,
    val location: String?,
    val imageUrl: String,
    val creationDate: String
)

sealed interface UserDetailUiState {

    data object Loading : UserDetailUiState

    data class Success(
        val user: UserDetailUiModel
    ) : UserDetailUiState

    data class Error(
        val message: String
    ) : UserDetailUiState
}

class UserDetailViewModel(
    userId: Int,
    repository: UserRepository
) : ViewModel() {

    val userDetailUiState: StateFlow<UserDetailUiState> =
        repository
            .getUserById(userId)
            .map { user ->

                val userUiState = UserDetailUiModel(
                    name = user.name,
                    reputation = user.reputation.toString(),
                    location = user.location ?: "Not provided",
                    imageUrl = user.profileImageUrl,
                    creationDate = user.creationDate.toString()
                )

                val state: UserDetailUiState =
                    UserDetailUiState.Success(userUiState)
                state
            }
            .catch { exception ->
                exception.message?.let { Log.d("Rabbia", it) }
                emit(
                    UserDetailUiState.Error(
                        exception.message
                            ?: "Failed to retrieve user"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UserDetailUiState.Loading
            )
}