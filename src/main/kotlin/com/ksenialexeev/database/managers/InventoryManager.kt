package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Inventory
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.InventoryMapper
import com.ksenialexeev.models.CreateInventoryDto
import com.ksenialexeev.models.InventoryDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface InventoryManager {
    suspend fun getAll(): List<InventoryDto?>
    suspend fun delete(id:Int):HttpStatusCode
    suspend fun create(dto: CreateInventoryDto):InventoryDto
    suspend fun update():InventoryDto
}

class InventoryManagerImpl : InventoryManager, KoinComponent {

    private val mapper by inject<InventoryMapper>()

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        Inventory.all().map(mapper::invoke)
    }

    override suspend fun delete(id: Int)= newSuspendedTransaction(Dispatchers.IO)  {
        Inventory.findById(id)?.let { it.delete();HttpStatusCode.OK } ?: throw NotFoundException("Inventory",id)
    }

    override suspend fun create(dto: CreateInventoryDto): InventoryDto {
        TODO("Not yet implemented")
    }

    override suspend fun update(): InventoryDto {
        TODO("Not yet implemented")
    }
}