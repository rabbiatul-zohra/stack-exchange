package com.example.stackexchange.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackexchange.usercore.data.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class BadgeUiState(
    val name: String,
    val rank: String
)

data class UserDetailUiModel(
    val name: String,
    val reputation: String,
    val location: String?,
    val imageUrl: String,
    val creationDate: String,
    val topTags: List<String>,
    val badges: List<BadgeUiState>
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
        combine(
            repository.getUserById(userId),
            repository.getUserTopTags(userId),
            repository.getUserBadges(userId),
        ) { user, topTags, badges, ->
                val userUiState = UserDetailUiModel(
                    name = user.name,
                    reputation = user.reputation.toString(),
                    location = user.location ?: "Not provided",
                    imageUrl = user.profileImageUrl,
                    creationDate = user.creationDate.toString(),
                    topTags = topTags.map { it.name },
                    badges = badges.map { badge ->
                        BadgeUiState(
                            name = badge.name,
                            rank = badge.rank
                        )
                    }
                )

                val state: UserDetailUiState = UserDetailUiState.Success(userUiState)

            state
            }
            .catch { exception ->
                emit(
                    UserDetailUiState.Error(
                        exception.message
                            ?: "Failed to retrieve user details"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UserDetailUiState.Loading
            )
}