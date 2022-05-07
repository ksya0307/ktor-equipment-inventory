package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.ClassroomsEquipment
import com.ksenialexeev.models.ClassroomEquipmentDto
import com.ksenialexeev.models.CreateClassroomEquipmentDto
import com.ksenialexeev.models.EquipmentSpecsDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClassroomEquipmentMapper : KoinComponent{
    val classroomMapper by inject<ClassroomMapper>()
    val equipmentMapper by inject<EquipmentMapper>()

    operator fun invoke(classroomsEquipment: ClassroomsEquipment) =
        classroomsEquipment.classroom?.let { classroomMapper(classroom = it) }?.let {
            classroomsEquipment.number_in_classroom?.let { it1 ->
                ClassroomEquipmentDto(
                    id = classroomsEquipment.id.value,
                    inventory_number = classroomsEquipment.inventory_number,
                    classroom = it,
                    equipment = equipmentMapper(equipment = classroomsEquipment.equipment),
                    number_in_classroom = it1,
                    equipment_type = classroomsEquipment.equipment_type
                )
            }
        }
}

class EquipmentSpecsMapper:KoinComponent{
    val classroomMapper by inject<ClassroomMapper>()
    val equipmentMapper by inject<EquipmentMapper>()

    operator fun invoke(classroomsEquipment: ClassroomsEquipment) = classroomsEquipment.number_in_classroom?.let {
        classroomsEquipment.classroom?.let { it1 -> classroomMapper(classroom = it1) }?.let { it2 ->
            EquipmentSpecsDto(
            inventory_number = classroomsEquipment.inventory_number,
            classroom = it2,
            number_in_classroom = it,
            equipment = equipmentMapper(equipment = classroomsEquipment.equipment),
            equipment_type = classroomsEquipment.equipment_type

            )
        }
    }
}