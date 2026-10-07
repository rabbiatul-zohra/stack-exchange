package com.example.stackexchange.user.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.stackexchange.user.presentation.BadgeUiState
import com.example.stackexchange.user.presentation.UserDetailUiState
import com.example.stackexchange.user.presentation.UserDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    onBackClick: () -> Unit,
    viewModel: UserDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.userDetailUiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("User")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(40.dp)
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

                    Row {
                        Text(text = "Top Tags: ")
                        if (state.user.topTags.isEmpty()) {
                            Text("No tags available")
                        } else {
                            Text(
                                text = state.user.topTags.joinToString(", ")
                            )
                        }
                    }
                    Text("Location: ${state.user.location ?: ""}")
                    Text("Creation date: ${state.user.creationDate}")
                    Text(text = "Badges: ")
                    Spacer(modifier = Modifier.height(6.dp))

                    if (state.user.badges.isEmpty()) {
                        Text("No badges available")
                    } else {
                        state.user.badges.forEach { badge ->
                            BadgeItem(badge)
                        }
                    }
                }

                is UserDetailUiState.Error -> {
                    Text(state.message)
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(
    badge: BadgeUiState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = badge.name)
        Spacer(modifier = Modifier.height(6.dp))

        Text(text = badge.rank.replaceFirstChar { it.uppercase() })
    }
}