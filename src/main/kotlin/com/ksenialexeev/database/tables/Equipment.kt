package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption


object Equipments: IntIdTable(){
    val description = text("description")
    val category = reference(
        "category",
        Categories,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
}

class Equipment (id:EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Equipment>(Equipments)

    var description by Equipments.description
    var category by Category referencedOn Equipments.category
}