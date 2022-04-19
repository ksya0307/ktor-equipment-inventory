package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Comment
import com.ksenialexeev.mappers.user.UserMapper
import com.ksenialexeev.models.CommentDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CommentMapper : KoinComponent {
    val inventoryMapper by inject<InventoryMapper>()
    val userMapper by inject<UserMapper>()

    operator fun invoke(comment: Comment) =
        CommentDto(
            id = comment.id.value,
            inventory = inventoryMapper(inventory = comment.inventory)!!,
            user = userMapper(user = comment.user),
            comment = comment.comment,
            datetime = comment.datetime,
        )
    }