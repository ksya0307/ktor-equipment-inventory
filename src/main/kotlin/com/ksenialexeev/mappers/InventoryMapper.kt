package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Inventory
import com.ksenialexeev.models.InventoryDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InventoryMapper : KoinComponent {
    val classroomEquipmentMapper by inject<ClassroomEquipmentMapper>()
    val userMapper by inject<UserMapper>()
    val classroomMapper by inject<ClassroomMapper>()

    operator fun invoke(inventory: Inventory) = inventory.given?.let {
        InventoryDto(
        id = inventory.id.value,
        inventory_number = classroomEquipmentMapper(classroomsEquipment = inventory.inventory_number),
        get_date = inventory.get_date,
        document = inventory.document,
        ifo = inventory.ifo,
        for_classroom = classroomMapper(classroom = inventory.for_classroom),
        responsible_person = userMapper(user = inventory.responsible_person),
        given = it
        )
    }
}