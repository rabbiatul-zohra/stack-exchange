package com.example.stackexchange

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stackexchange.ui.theme.StackexchangeTheme
import com.example.stackexchange.user.presentation.UsersViewModel
import com.example.stackexchange.user.ui.SearchUsers
import com.example.stackexchange.usercore.RetrofitClient
import com.example.stackexchange.usercore.data.UserRepositoryImpl
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val repository by lazy {
        UserRepositoryImpl(
            service = RetrofitClient.api
        )
    }

    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModelFactory(repository)
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackexchangeTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Stack Exchange")
                        SearchUsers(viewModel = usersViewModel)
                    }
                }
            }
        }
    }
}