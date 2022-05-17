package com.ksenialexeev.database.tables


import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import java.util.*

object RepairEquipments: IntIdTable("repair_equipment"){
    val repair_id = reference(
        "repair_id",
        Repairs,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val equipment_id  =  reference(
        "equipment_id",
        ClassroomsEquipments,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val problem = text("problem")
}

class RepairEquipment(id: EntityID<Int>) : IntEntity(id) {
    companion object:IntEntityClass<RepairEquipment>(RepairEquipments)

    var repair_id by Repair referencedOn RepairEquipments.repair_id
    var equipment_id by ClassroomsEquipment referencedOn RepairEquipments.equipment_id
    var problem by RepairEquipments.problem
}