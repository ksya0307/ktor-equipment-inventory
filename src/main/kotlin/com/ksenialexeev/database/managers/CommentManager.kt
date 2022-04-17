package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Comment
import com.ksenialexeev.database.tables.Comments
import com.ksenialexeev.database.tables.Inventory
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.CommentMapper
import com.ksenialexeev.models.CommentDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface CommentManager {
    suspend fun getAllComments(): List<CommentDto>
    suspend fun getCommentByInventory(id: Int): List<CommentDto>
}

class CommentManagerImpl : CommentManager, KoinComponent {
    private val mapper by inject<CommentMapper>()

    override suspend fun getAllComments() = newSuspendedTransaction(Dispatchers.IO) {
        Comment.all().map(mapper::invoke)
    }

    override suspend fun getCommentByInventory(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        val inventory: Inventory = Inventory.findById(id) ?: throw NotFoundException("Inventory", id)
        Comment.find{
            Comments.inventory eq inventory.id
        }.map(mapper::invoke)
    }

}