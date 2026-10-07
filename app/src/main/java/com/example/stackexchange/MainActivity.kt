package com.example.stackexchange

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.stackexchange.navigation.AppNavHost
import com.example.stackexchange.ui.theme.StackexchangeTheme
import com.example.stackexchange.user.presentation.UsersViewModel
import com.example.stackexchange.network.RetrofitClient
import com.example.stackexchange.usercore.data.UserRepositoryImpl
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val repository by lazy {
        UserRepositoryImpl(
            service = RetrofitClient.api
        )
    }

    private val usersViewModel: UsersViewModel by viewModels {
        ViewModelFactory(repository)
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackexchangeTheme() {
            AppNavHost(usersViewModel, repository)
            }
        }
    }
}