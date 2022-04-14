package com.ksenialexeev.models

import com.ksenialexeev.database.tables.User
import kotlinx.serialization.Serializable

@Serializable
data class ClassroomDto(val number:String, val user: UserDto)
