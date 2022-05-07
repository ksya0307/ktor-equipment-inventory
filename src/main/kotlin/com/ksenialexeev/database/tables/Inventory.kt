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
    val document = reference("document",
    Documents, onUpdate = ReferenceOption.CASCADE,
    onDelete = ReferenceOption.CASCADE)
    val ifo = reference("ifo",
        Ifos, onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE)
    val for_classroom = reference(
        "for_classroom",
        Classrooms,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val by_request = bool("by_request").nullable()
}
class Inventory (id: EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Inventory>(Inventories)

    var inventory_number by ClassroomsEquipment referencedOn Inventories.inventory_number
    var get_date by Inventories.get_date
    var given by Inventories.given
    var document by Document referencedOn Inventories.document
    var ifo by Ifo referencedOn Inventories.ifo
    var for_classroom by Classroom referencedOn Inventories.for_classroom
    var by_request by Inventories.by_request
}