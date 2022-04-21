package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption


//Таблица
object ClassroomsEquipments:IntIdTable("classrooms_equipment"){
    val inventory_number = long("inventory_number")
    val classroom = reference(
        "classroom",
        Classrooms,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val equipment = reference(
        "equipment", Equipments,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val number_in_classroom = varchar("number_in_classroom", 7)
    val equipment_type = enumeration("equipment_type", EquipmentBelonging::class)
}

//Строка таблицы
class ClassroomsEquipment(id: EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, ClassroomsEquipment> (ClassroomsEquipments)

    var inventory_number by ClassroomsEquipments.inventory_number
    var classroom by Classroom referencedOn ClassroomsEquipments.classroom
    var equipment by Equipment referencedOn ClassroomsEquipments.equipment
    var number_in_classroom by ClassroomsEquipments.number_in_classroom
    var equipment_type by ClassroomsEquipments.equipment_type
}

enum class EquipmentBelonging (val type:String) {
    LAB( "Учебно-лабораторное оборудование"),
    PROD("Учебно-производственное оборудование"),
    OTHER("Другое")
}