package com.ksenialexeev.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Int,
    val comment: String,
    val inventory: InventoryDto,
    val user: UserDto,
    val datetime: LocalDateTime
)
