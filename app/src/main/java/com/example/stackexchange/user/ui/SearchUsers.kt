package com.example.stackexchange.user.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stackexchange.user.presentation.UiState
import com.example.stackexchange.user.presentation.UsersViewModel
import com.example.stackexchange.usercore.model.User

@Composable
fun SearchUsers(
    viewModel: UsersViewModel,
    onUserClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(36.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { newValue ->
                    query = newValue
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Search users")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Button(
                onClick = {
                    viewModel.search(query)
                },
                enabled = query.isNotBlank()
            ) {
                Text("Search")
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {

                UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    LazyColumn {
                        items(
                            items = currentState.users,
                            key = { it.id }
                        ) { user ->
                            UserRow(
                                user = user,
                                onClick = { onUserClick(user.id) }
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    Text(currentState.message)
                }

                UiState.Idle -> Unit
            }

        }
    }
}

@Composable
fun UserRow(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}