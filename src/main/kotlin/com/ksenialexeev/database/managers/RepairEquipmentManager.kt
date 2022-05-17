package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.*
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.RepairEquipmentMapper
import com.ksenialexeev.models.CreateRepairEquipmentDto
import com.ksenialexeev.models.RepairEquipmentDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RepairEquipmentManager {
    suspend fun getAll(): List<RepairEquipmentDto>
    suspend fun create(dto: CreateRepairEquipmentDto): RepairEquipmentDto
    suspend fun update(repair_id: Int, equipment_id: Int): RepairEquipmentDto
    suspend fun delete(id: Int): HttpStatusCode
    suspend fun getByUserId(user_id: Int): List<RepairEquipmentDto>
}

class RepairEquipmentManagerImpl : RepairEquipmentManager, KoinComponent {

    private val mapper by inject<RepairEquipmentMapper>()

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        RepairEquipment.all().map(mapper::invoke)
    }

    override suspend fun create(dto: CreateRepairEquipmentDto) = newSuspendedTransaction(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

    override suspend fun update(repair_id: Int, equipment_id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        TODO("Not yet implemented")
    }

    override suspend fun getByUserId(user_id: Int): List<RepairEquipmentDto> {
        TODO("Not yet implemented")
    }

}