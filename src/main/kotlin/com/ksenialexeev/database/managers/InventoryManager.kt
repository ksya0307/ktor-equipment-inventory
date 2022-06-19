package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.*
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.*
import com.ksenialexeev.models.ClassroomDto
import com.ksenialexeev.models.ClassroomEquipmentDto
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
    private val clEqMapper by inject<ClassroomEquipmentMapper>()
    private val docMapper by inject<DocumentMapper>()
    private val ifoMapper by inject<IfoMapper>()
    private val classroomMapper by inject<ClassroomMapper>()

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
            }.let {

                it.given?.let { it1 ->
                    clEqMapper(it.inventory_number)?.let { it2 ->
                        classroomMapper(it.for_classroom)?.let { it3 ->
                            it.by_request?.let { it4 ->
                                InventoryDto(
                                    id = it.id.value,
                                    inventory_number = it2,
                                    get_date = it.get_date,
                                    document = docMapper(it.document),
                                    ifo = ifoMapper(it.ifo),
                                    for_classroom = it3,
                                    given = it1,
                                    by_request = it4
                                )
                            }
                        }
                    }
                }
            }
        } else {
            throw NotFoundException("Inventory Already Done", "Make up a new one")
        }
    }

    override suspend fun update(): InventoryDto {
        TODO("Not yet implemented")
    }
}