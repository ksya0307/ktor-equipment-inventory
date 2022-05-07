package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Ifos:IntIdTable("ifos"){
    val name = varchar("name",64)
}

class Ifo(id:EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Ifo>(Ifos)

    var name by Ifos.name
}