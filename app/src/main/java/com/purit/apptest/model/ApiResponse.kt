package com.purit.apptest.model

data class ApiResponse<T>(
    val success: Boolean,
    val data: T
)
