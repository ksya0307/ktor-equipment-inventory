package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.ClassroomsEquipment
import com.ksenialexeev.models.ClassroomEquipmentDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClassroomEquipmentMapper : KoinComponent{
    val classroomMapper by inject<ClassroomMapper>()
    val equipmentMapper by inject<EquipmentMapper>()

    operator fun invoke(classroomsEquipment: ClassroomsEquipment) = ClassroomEquipmentDto(
        id = classroomsEquipment.id.value,
        inventory_number = classroomsEquipment.inventory_number,
        classroom = classroomMapper(classroom = classroomsEquipment.classroom),
        equipment = equipmentMapper(equipment = classroomsEquipment.equipment),
        number_in_classroom = classroomsEquipment.number_in_classroom,
        equipment_type = classroomsEquipment.equipment_type.type
    )
}