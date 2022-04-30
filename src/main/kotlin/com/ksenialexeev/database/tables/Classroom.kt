package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object Classrooms: IdTable<String>("classrooms"){
    override val id: Column<EntityID<String>> = varchar("number",10).entityId()
    val user = reference(
        "user", Users,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    override val primaryKey:PrimaryKey = PrimaryKey(id)
}
class Classroom(id: EntityID<String>): Entity<String>(id) {
    companion object: EntityClass<String, Classroom>(Classrooms)

    var user by User referencedOn Classrooms.user
}