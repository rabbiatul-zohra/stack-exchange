package com.example.stackexchange.user.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
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
                AsyncImage(
                    model = state.user.imageUrl,
                    contentDescription = "${state.user.name} profile image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text("Username: ${state.user.name}")
                Text("Reputation: ${state.user.reputation}")
                Text("Location: ${state.user.location ?: ""}")
                Text("Creation date: ${state.user.creationDate}")
            }

            is UserDetailUiState.Error -> {
                Text(state.message)
            }
        }
    }
}