package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Repair
import com.ksenialexeev.database.tables.RepairEquipment
import com.ksenialexeev.models.RepairDto
import com.ksenialexeev.models.RepairEquipmentDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RepairMapper: KoinComponent {
    operator fun invoke(repair: Repair) = RepairDto(
        phone = repair.phone
    )
}

class RepairEquipmentMapper: KoinComponent{
    val repairMapper by inject<RepairMapper>()
    val classroomEquipmentMapper by inject<ClassroomEquipmentMapper>()

    operator fun invoke(repairEquipment: RepairEquipment) =RepairEquipmentDto(
        id = repairEquipment.id.value,
        repair = repairMapper(repair = repairEquipment.repair_id),
        equipment = classroomEquipmentMapper(classroomsEquipment =  repairEquipment.equipment_id),
        problem = repairEquipment.problem
    )
    }
