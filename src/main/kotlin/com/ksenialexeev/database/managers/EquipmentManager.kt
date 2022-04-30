package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Categories
import com.ksenialexeev.database.tables.Category
import com.ksenialexeev.database.tables.Equipment
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.EquipmentMapper
import com.ksenialexeev.models.CreateEquipmentDto
import com.ksenialexeev.models.EquipmentDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface EquipmentManager {
    suspend fun getById(id: Int): EquipmentDto
    suspend fun create(dto: CreateEquipmentDto): EquipmentDto
    suspend fun getAll(): List<EquipmentDto>
}

class EquipmentManagerImpl : EquipmentManager, KoinComponent {

    private val mapper by inject<EquipmentMapper>()

    override suspend fun getById(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Equipment.findById(id)?.let { mapper(it) } ?: throw NotFoundException("Equipment", id)
    }

    override suspend fun create(dto: CreateEquipmentDto) = newSuspendedTransaction(Dispatchers.IO) {
        val categoryId = Category.find { Categories.name eq dto.category.name }
        println(categoryId)
        Equipment.new {
            description = dto.description
            category = categoryId.firstOrNull() ?: throw NotFoundException("Equipment", categoryId)
        }.let(mapper::invoke)
    }

    override suspend fun getAll(): List<EquipmentDto> = newSuspendedTransaction(Dispatchers.IO){
        Equipment.all().map(mapper::invoke)
    }

}