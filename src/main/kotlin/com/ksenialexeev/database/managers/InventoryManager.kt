package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.*
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
    suspend fun delete(id: Int): HttpStatusCode
    suspend fun create(dto: CreateInventoryDto): InventoryDto?
    suspend fun update(): InventoryDto
}

class InventoryManagerImpl : InventoryManager, KoinComponent {

    private val mapper by inject<InventoryMapper>()

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        Inventory.all().map(mapper::invoke)
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Inventory.findById(id)?.let { it.delete();HttpStatusCode.OK } ?: throw NotFoundException(
            "Inventory not found",
            id
        )
    }

    override suspend fun create(dto: CreateInventoryDto) = newSuspendedTransaction(Dispatchers.IO) {
        var classroomEquipment = ClassroomsEquipment.findById(dto.inventory_number) ?: throw  NotFoundException(
            "Inventory Number not found",
            dto.inventory_number
        )
        var documentEntity = Document.findById(dto.document)    ?: throw NotFoundException("Document Not Found", dto.ifo)
        var ifoEntity =
            Ifo.findById(dto.ifo) ?: throw NotFoundException("IFO Not Found", dto.ifo)
        var classroom = Classroom.find { Classrooms.id eq dto.for_classroom }.firstOrNull()
            ?: throw NotFoundException("Classroom Not Found", dto.for_classroom)
        var inventory = Inventory.find { Inventories.inventory_number eq classroomEquipment.id }
        if (inventory.empty()) {
            Inventory.new {
                inventory_number = classroomEquipment
                get_date = dto.get_date
                document = documentEntity
                ifo = ifoEntity
                for_classroom = classroom
                given = dto.given
                by_request = dto.by_request
            }.let { mapper(it) }
        } else {
            throw NotFoundException("Inventory Already Done", "Make up a new one")
        }
    }

    override suspend fun update(): InventoryDto {
        TODO("Not yet implemented")
    }
}