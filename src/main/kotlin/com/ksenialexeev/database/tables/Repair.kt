package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Repairs : IntIdTable("repair") {
    val problem = text("problem")
    val inventory_number = reference(
        "inventory_number",
        ClassroomsEquipments,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val phone = varchar("phone",16)
    val datetime = date("datetime")
}

class Repair(id: EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Repair>(Repairs)

    var problem by Repairs.problem
    var inventory_number by ClassroomsEquipment referencedOn Repairs.inventory_number
    var phone by Repairs.phone
    var datetime by Repairs.datetime
}