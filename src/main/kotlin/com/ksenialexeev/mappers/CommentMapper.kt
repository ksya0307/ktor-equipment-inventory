package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Comment
import com.ksenialexeev.mappers.UserMapper
import com.ksenialexeev.models.CommentDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CommentMapper : KoinComponent {
    val inventoryMapper by inject<InventoryMapper>()
    val userMapper by inject<UserMapper>()

    operator fun invoke(comment: Comment) =
        userMapper(user = comment.user)?.let {
            CommentDto(
                id = comment.id.value,
                inventory = inventoryMapper(inventory = comment.inventory)!!,
                user = it,
                comment = comment.comment,
                datetime = comment.datetime,
            )
        }
    }