package com.purit.apptest.models

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val user: UserInfo,
    val token: String,
    val expires_at: String
)

data class UserInfo(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?
)