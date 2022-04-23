package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Inventories:IntIdTable("inventory"){
    val inventory_number = reference(
        "inventory_number",
        ClassroomsEquipments,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val get_date = date("get_date")
    val given = bool("given").nullable()
    val document = text("document")
    val ifo = varchar("ifo",64)
    val for_classroom = reference(
        "for_classroom",
        Classrooms,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
}
class Inventory (id: EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Inventory>(Inventories)

    var inventory_number by ClassroomsEquipment referencedOn Inventories.inventory_number
    var get_date by Inventories.get_date
    var given by Inventories.given
    var document by Inventories.document
    var ifo by Inventories.ifo
    var for_classroom by Classroom referencedOn Inventories.for_classroom
}