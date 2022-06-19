package com.ksenialexeev.models

import com.ksenialexeev.dateTimeInUtc
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

@Serializable
data class CreateCommentDto(
    val comment: String,
    val inventoryId: Int,
    val userId: Int,
    val datetime: String? = dateTimeInUtc.toString()
)
