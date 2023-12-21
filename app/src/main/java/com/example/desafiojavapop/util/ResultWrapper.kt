package com.example.desafiojavapop.util

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data object Empty : ResultWrapper<Nothing>()
    data class Failure(val exception: Exception) : ResultWrapper<Nothing>()
}

