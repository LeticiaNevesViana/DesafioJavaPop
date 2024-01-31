package com.example.desafiojavapop.util

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Failure(val exception: Exception, val message: String? = null, val statusCode: Int? = null) : ResultWrapper<Nothing>()
    object NetworkError : ResultWrapper<Nothing>()

}

