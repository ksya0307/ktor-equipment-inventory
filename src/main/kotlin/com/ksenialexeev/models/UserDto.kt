package com.ksenialexeev.models

import com.ksenialexeev.database.tables.Role
import com.ksenialexeev.database.tables.Users
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val surname: String,
    val name: String,
    val patronymic: String? = null,
    val username: String,
    val role: Role
)

@Serializable
data class CreateUserDto(
    val surname: String,
    val name: String,
    val patronymic: String? = null,
    val username: String,
    val password: String,
    val role: Role? = Role.COMMON
)


@Serializable
data class ChangeUserDto(
    val id: Int,
    val surname: String? = null,
    val name: String? = null,
    val patronymic: String? = null,
    val username: String? = null,
    val password: String? = null,
    val role: Role? = null
)

@Serializable
data class ChangePasswordDto(
    val password: String
)

@Serializable
data class UserLoginDto(val username: String, val password: String)

@Serializable
data class TokenPair(val accessToken: String, val refreshToken: String)