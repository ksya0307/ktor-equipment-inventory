package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Categories
import com.ksenialexeev.database.tables.Category
import com.ksenialexeev.database.tables.Equipment
import com.ksenialexeev.database.tables.Equipments
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.EquipmentMapper
import com.ksenialexeev.models.CreateEquipmentDto
import com.ksenialexeev.models.EquipmentDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface EquipmentManager {
    suspend fun getById(id: Int): EquipmentDto
    suspend fun create(dto: CreateEquipmentDto): EquipmentDto
    suspend fun getAll(): List<EquipmentDto>
    suspend fun delete(id: Int): HttpStatusCode
    suspend fun update(id: Int, description: String, categoryId: Int): EquipmentDto
    suspend fun getByCategory(categoryId:Int):List<EquipmentDto>
}

class EquipmentManagerImpl : EquipmentManager, KoinComponent {

    private val mapper by inject<EquipmentMapper>()

    override suspend fun getById(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Equipment.findById(id)?.let { mapper(it) } ?: throw NotFoundException("Equipment not found", id)
    }

    override suspend fun create(dto: CreateEquipmentDto) = newSuspendedTransaction(Dispatchers.IO) {
        val categoryEntity =
            Category.findById(dto.category) ?: throw NotFoundException("Category not found:", dto.category)
        val descriptionEntity = Equipment.find { Equipments.description.lowerCase() eq dto.description.lowercase() }
        if (descriptionEntity.empty()) {
            Equipment.new {
                description = dto.description
                category = categoryEntity
            }.let(mapper::invoke)
        } else {
            throw NotFoundException("Equipment already exists", dto.description)
        }

    }

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        Equipment.all().map(mapper::invoke)
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Equipment.findById(id)?.let { it.delete(); HttpStatusCode.OK }
            ?: throw NotFoundException("Equipment not found:", id)
    }

    override suspend fun update(id: Int, description: String, categoryId: Int) =
        newSuspendedTransaction(Dispatchers.IO) {
            val equipment = Equipment.find { Equipments.description.lowerCase() eq description.lowercase() }
            val category = Category.findById(categoryId) ?: throw NotFoundException("Equipment not found:", categoryId)
            if (equipment.empty()) {
                Equipment.findById(id)?.let {
                    it.description = description
                    it.category = category
                    mapper(it)
                } ?: throw NotFoundException("Equipment with id $id not found", "")
            } else {
                throw NotFoundException("Equipment already exists", description)
            }
        }

    override suspend fun getByCategory(categoryId: Int)= newSuspendedTransaction(Dispatchers.IO){
        val category = Category.findById(categoryId) ?:  throw NotFoundException("Category not found", categoryId)
       Equipment.all().filter { it.category == category }.map(mapper::invoke)

    }

}