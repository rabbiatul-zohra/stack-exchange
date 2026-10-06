package com.example.stackexchange.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackexchange.usercore.data.UserRepository
import com.example.stackexchange.usercore.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Idle: UiState
    data object Loading : UiState

    data class Success(
        val users: List<User>
    ) : UiState

    data class Error(
        val message: String
    ) : UiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val searchRequests = MutableSharedFlow<String>()

    val uiState: StateFlow<UiState> =
        searchRequests.flatMapLatest {
            repository.getUsers(it)
            .map { users ->
                val state: UiState =
                    UiState.Success(users)

                state
            }
                .onStart { emit(UiState.Loading) }
            .catch { exception ->
                emit(
                    UiState.Error(
                        exception.message ?: "Failed to retrieve users"
                    )
                )
            }
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Idle
            )

    fun search(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            searchRequests.emit(query.trim())
        }
    }
}