package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import java.time.Instant.now

object Repairs : IntIdTable("repair") {
    val phone = varchar("phone",18)
    val datetime = date("datetime")
    val completed = bool("completed")
}

class Repair(id: EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Repair>(Repairs)
    var phone by Repairs.phone
    var datetime by Repairs.datetime
    var completed by Repairs.completed
}