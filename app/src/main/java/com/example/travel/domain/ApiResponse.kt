package com.example.travel.domain

sealed class ApiResponse<out T> {
    data class Success<out T>(val value: T) : ApiResponse<T>()
    data class Failure(
        val code: Int?,
        val message: String
    ) : ApiResponse<Nothing>()
}

data class ApiResponseError(
    val message: List<String>,
    val error: String,
    val statusCode: Int
)