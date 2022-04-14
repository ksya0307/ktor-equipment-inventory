package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object Categories: IntIdTable("categories"){
    val name = varchar("name", length = 64)
}
class Category(id: EntityID<Int>): IntEntity(id){
    companion object: EntityClass<Int, Category>(Categories)

    var name by Categories.name
}
