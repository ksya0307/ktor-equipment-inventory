package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.*
import com.ksenialexeev.exceptions.NotFoundClassroomException
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.ClassroomEquipmentMapper
import com.ksenialexeev.models.ClassroomEquipmentDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ClassroomEquipmentManager {
    suspend fun getAllClassroomEquipment(classroom: String?, equipmentCategory: String?): List<ClassroomEquipmentDto>
}

class ClassroomEquipmentManagerImpl : ClassroomEquipmentManager, KoinComponent {
    private val mapper by inject<ClassroomEquipmentMapper>()
    override suspend fun getAllClassroomEquipment(
        classroom: String?,
        equipmentCategory: String?
    ): List<ClassroomEquipmentDto> =
        newSuspendedTransaction(Dispatchers.IO) {
            val eqi: Equipment? = equipmentCategory?.let { category ->
                Category.find { Categories.name eq category }.firstOrNull()?.let {
                    Equipment.find { Equipments.category eq it.id }.firstOrNull()
                        ?: throw NotFoundException("Equipment", it.id.value)
                } ?: throw NotFoundException("Category", category)
            }
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
                } else {
                    ClassroomsEquipment.all()
                }
            }.map(mapper::invoke)
        }
}
