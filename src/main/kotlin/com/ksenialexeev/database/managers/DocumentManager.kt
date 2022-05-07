package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Document
import com.ksenialexeev.database.tables.Documents
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.DocumentMapper
import com.ksenialexeev.models.ChangeDocumentDto
import com.ksenialexeev.models.DocumentDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface DocumentManager {
    suspend fun getAll(): List<DocumentDto>
    suspend fun create(dto: DocumentDto): DocumentDto
    suspend fun update(dto: ChangeDocumentDto): DocumentDto
    suspend fun delete(id: Int): HttpStatusCode
}

class DocumentManagerImpl : DocumentManager, KoinComponent {

    private val mapper by inject<DocumentMapper>()

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        Document.all().map(mapper::invoke)
    }

    override suspend fun create(dto: DocumentDto) = newSuspendedTransaction(Dispatchers.IO) {
        val document = Document.find { Documents.name eq dto.name }
        if (document.empty()) {
            Document.new {
                name = dto.name
            }.let { mapper(it) }
        } else {
            throw NotFoundException("Document already exists", dto.name)
        }
    }

    override suspend fun update(dto: ChangeDocumentDto) = newSuspendedTransaction(Dispatchers.IO) {
        val document = Document.find { Documents.name eq dto.name }
        if (document.empty()) {
            Document.findById(dto.id)?.let {
                it.name = dto.name
                mapper(it)
            } ?: throw NotFoundException("Document", id)
        } else {
            throw NotFoundException("Document already exists", dto.name)
        }

    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Document.findById(id)?.let { it.delete(); HttpStatusCode.OK } ?: throw  NotFoundException("Document", id)
    }

}