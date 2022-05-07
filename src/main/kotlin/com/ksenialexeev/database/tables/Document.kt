package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Documents: IntIdTable("documents"){
    val name = varchar("name",128)
}
class Document(id:EntityID<Int>):IntEntity(id){
    companion object: EntityClass<Int,Document>(Documents)

    var name by Documents.name
}