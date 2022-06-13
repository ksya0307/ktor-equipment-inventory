package com.ksenialexeev.database.tables

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.time.Instant.now

object Repairs : IntIdTable("repair") {
    val phone = varchar("phone",18)
    val datetime = datetime("datetime")
//        .also {
//        it.defaultValueFun = { Clock.System.now().toLocalDateTime(TimeZone.UTC).date } }
    val completed = bool("completed").default(false)
}

class Repair(id: EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Repair>(Repairs)
    var phone by Repairs.phone
    var datetime by Repairs.datetime
    var completed by Repairs.completed
}