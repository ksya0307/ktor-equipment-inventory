package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Inventory
import com.ksenialexeev.models.InventoryDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InventoryMapper : KoinComponent {
    val classroomEquipmentMapper by inject<ClassroomEquipmentMapper>()
    val classroomMapper by inject<ClassroomMapper>()
    val documentMapper by inject<DocumentMapper>()
    val ifoManager by inject<IfoMapper>()

    operator fun invoke(inventory: Inventory) = inventory.given?.let {
        inventory.by_request?.let { it1 ->
            classroomEquipmentMapper(classroomsEquipment = inventory.inventory_number)?.let { it2 ->
                InventoryDto(
                    id = inventory.id.value,
                    inventory_number = it2,
                    get_date = inventory.get_date,
                    document = documentMapper(document = inventory.document),
                    ifo = ifoManager(ifo = inventory.ifo),
                    for_classroom = classroomMapper(classroom = inventory.for_classroom),
                    given = it,
                    by_request = it1
                )
            }
        }
    }
}
