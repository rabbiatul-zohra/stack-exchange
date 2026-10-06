package com.example.stackexchange.usercore

sealed interface Response<out T> {

    data class Success<T>(
        val data: T
    ) : Response<T>

    data class Error(
        val exception: Throwable
    ) : Response<Nothing>
}