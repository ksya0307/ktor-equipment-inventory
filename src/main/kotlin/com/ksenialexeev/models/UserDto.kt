package com.ksenialexeev.models

import com.ksenialexeev.database.tables.Role
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val surname: String,
    val name: String,
    val patronymic: String,
    val role: Role
) {
    val fullName = "$surname $name $patronymic"
}

@Serializable
data class UserLoginDto(val username: String, val password: String)

@Serializable
data class TokenPair(val accessToken: String, val refreshToken: String)

@Serializable
data class RefreshToken(val refreshToken: String)
