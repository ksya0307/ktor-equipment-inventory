package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Comment
import com.ksenialexeev.database.tables.Comments
import com.ksenialexeev.database.tables.Inventory
import com.ksenialexeev.database.tables.User
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.CommentMapper
import com.ksenialexeev.models.CommentDto
import com.ksenialexeev.models.CreateCommentDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface CommentManager {
    suspend fun getAllComments(): List<CommentDto?>
    suspend fun getCommentByInventory(id: Int): List<CommentDto?>
    suspend fun delete(id: Int):HttpStatusCode
    suspend fun update(id:Int, comment: String?):CommentDto
    suspend fun create(dto: CreateCommentDto):CommentDto?
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

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Comment.findById(id)?.let { it.delete();HttpStatusCode.OK }?: throw NotFoundException("Comment not found:",id)
    }

    override suspend fun update(id: Int, comment: String?) = newSuspendedTransaction(Dispatchers.IO) {
        Comment.findById(id)?.let {
            if (comment != null) {
                it.comment = comment
            }
            mapper(it)
        } ?: throw NotFoundException("Comment not found", id)
    }

    override suspend fun create(dto: CreateCommentDto) = newSuspendedTransaction(Dispatchers.IO) {
        Comment.new {
            comment = dto.comment
            datetime = dto.datetime!!.toLocalDateTime()
            user = User.findById(dto.userId)!!
            inventory = Inventory.findById(dto.inventoryId)!!
        }.let{mapper(it)}
    }

}