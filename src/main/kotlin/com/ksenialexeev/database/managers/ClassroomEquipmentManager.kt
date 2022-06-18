package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.*
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.ClassroomEquipmentMapper
import com.ksenialexeev.mappers.EquipmentSpecsMapper
import com.ksenialexeev.models.ClassroomEquipmentDto
import com.ksenialexeev.models.CreateClassroomEquipmentDto
import com.ksenialexeev.models.EquipmentSpecsDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ClassroomEquipmentManager {
    suspend fun getAllClassroomEquipmentByClassroomAndCategory(
        classroom: String?,
        equipmentCategory: String?
    ): List<ClassroomEquipmentDto?>
    suspend fun getSpecsById(id: Int): EquipmentSpecsDto
    suspend fun create(dto: CreateClassroomEquipmentDto): ClassroomEquipmentDto?
    suspend fun update(
        id: Int,
        inventory_number: Long?,
        classroom: String?,
        equipment: String?,
        number_in_classroom: String?,
        equipment_type: EquipmentBelonging?
    ): ClassroomEquipmentDto?

    suspend fun delete(id: Int): HttpStatusCode
    suspend fun getUsersEquipmentInClassrooms(userId: Int): List<ClassroomEquipmentDto?>
}

class ClassroomEquipmentManagerImpl : ClassroomEquipmentManager, KoinComponent {

    private val mapper by inject<ClassroomEquipmentMapper>()
    private val mapperSpecs by inject<EquipmentSpecsMapper>()

    override suspend fun getAllClassroomEquipmentByClassroomAndCategory(

        classroom: String?,
        equipmentCategory: String?
    ): List<ClassroomEquipmentDto?> =
        newSuspendedTransaction(Dispatchers.IO) {
            val eqi: Equipment? = equipmentCategory?.let { category ->
                Category.find { Categories.name eq category }.firstOrNull()?.let {
                    Equipment.find { Equipments.category eq it.id }.firstOrNull()
                        ?: throw NotFoundException("Equipment with id ${it.id.value} not found", "")
                } ?: throw NotFoundException("Category not found", category)
            }

            val classroomEntity: Classroom? = classroom?.let { Classroom.findById(it) }
            run {
                if (classroom == null && eqi != null) {
                    ClassroomsEquipment.find {
                        (ClassroomsEquipments.equipment eq eqi.id)
                    }
                } else if (classroom != null && eqi == null) {
                    ClassroomsEquipment.find {
                        (ClassroomsEquipments.classroom eq classroom)
                    }
                } else if (classroom != null && eqi != null) {
                    ClassroomsEquipment.find {
                        (ClassroomsEquipments.classroom eq classroom) and (ClassroomsEquipments.equipment eq eqi.id)
                    }
                }
                else {
                    ClassroomsEquipment.all()
                }
            }.map(mapper::invoke)
        }

    override suspend fun getSpecsById(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        ClassroomsEquipment.findById(id)?.let {
            mapperSpecs(it)
        } ?: throw NotFoundException("Cannot find these specs", "")
    }

    override suspend fun create(dto: CreateClassroomEquipmentDto) = newSuspendedTransaction(Dispatchers.IO) {

        val classroomId = Classroom.find { Classrooms.id eq dto.classroom.toString() }.firstOrNull()
        println("classroomId - ${classroomId?.id}")
        val equipmentEnt = Equipment.findById(dto.equipment) ?: throw NotFoundException("Equipment not found", dto.equipment)
        println("eq $equipmentEnt")
        val existingInventoryNumber =
            ClassroomsEquipment.find { ClassroomsEquipments.inventory_number eq dto.inventory_number }
        if (existingInventoryNumber.empty()) {
            ClassroomsEquipment.new {
                inventory_number = dto.inventory_number
                if (classroomId != null) {
                    classroom = classroomId
                }

                    equipment = equipmentEnt

                if (dto.number_in_classroom != null) {
                    number_in_classroom = dto.number_in_classroom.toString()
                }
                equipment_type = dto.equipment_type
            }.let { mapper(it) }
        } else {
            throw NotFoundException("Equipment already exists", dto.inventory_number)
        }
    }

    override suspend fun update(
        id: Int,
        inventory_number: Long?,
        classroom: String?,
        equipment: String?,
        number_in_classroom: String?,
        equipment_type: EquipmentBelonging?
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val classroom = Classroom.find { Classrooms.id eq classroom.toString() }.firstOrNull()
        val equipment = Equipment.find { Equipments.description eq equipment.toString() }.firstOrNull()
        ClassroomsEquipment.findById(id)?.let {
            if (inventory_number != null) {
                it.inventory_number = inventory_number
            }

            it.classroom = classroom
            if (number_in_classroom != null) {
                it.number_in_classroom = number_in_classroom
            }
            if (equipment != null) {
                it.equipment = equipment
            }
            if (equipment_type != null) {
                it.equipment_type = equipment_type
            }
            mapper(it)
        } ?: throw NotFoundException("This equipment already exists", "")
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        ClassroomsEquipment.findById(id)?.let { it.delete();HttpStatusCode.OK }
            ?: throw NotFoundException("ClassroomsEquipment", id)
    }

    override suspend fun getUsersEquipmentInClassrooms(userId: Int) = newSuspendedTransaction(Dispatchers.IO) {
        ClassroomsEquipments
            .innerJoin(Classrooms)
            .innerJoin(Users)
            .select { Classrooms.user eq userId }
            .withDistinct()
            .let {
                if (it.empty()) {
                    throw NotFoundException("User with id $userId not found", "Try another one")
                } else {
                    ClassroomsEquipment.wrapRows(it).map { equipments -> mapper(equipments) }
                }
            }
    }
}
