package com.example.stackexchange.user.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stackexchange.user.presentation.UserDetailUiState
import com.example.stackexchange.user.presentation.UserDetailViewModel

@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.userDetailUiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier.padding(32.dp)
    ) {
        when (val state = uiState) {

            UserDetailUiState.Loading -> {
                CircularProgressIndicator()
            }

            is UserDetailUiState.Success -> {
                
                Text(state.user.name)
                Text(state.user.reputation)
                Text(state.user.location ?: "")
                Text(state.user.creationDate)
            }

            is UserDetailUiState.Error -> {
                Text(state.message)
            }
        }
    }
}