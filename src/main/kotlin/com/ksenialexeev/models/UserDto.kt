package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val surname: String,
    val name: String,
    val patronymic: String
){
    val fullName = "$surname $name $patronymic"
}
