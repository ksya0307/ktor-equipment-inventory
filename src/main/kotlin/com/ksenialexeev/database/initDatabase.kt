package com.ksenialexeev.database

import com.ksenialexeev.database.tables.*
import com.ksenialexeev.database.tables.ClassroomsEquipments
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.Driver
import kotlin.reflect.jvm.jvmName

fun initDatabase() {
    // "jdbc:postgresql://db.mvsujbakvsofcokeiiod.supabase.co:5432/postgres"
   // "postgres"//
    //"yEVB8fCpvLY7dDzyF27F"//
    val url         = System.getenv("DB_URL") ?:  "jdbc:postgresql://ec2-23-20-224-166.compute-1.amazonaws.com:5432/db4st2jl7l7dve"
    val user        = System.getenv("DB_USERNAME") ?: "erxiyxrmuiyaqf"
    val password    = System.getenv("DB_PASSWORD") ?: "a4133c532236de7d8b9cf31c6512a2b8147176d74049b5de75d62fa0dde16eb3"

    Database.connect(
        url = url,
        user = user,
        password = password,
        driver = Driver::class.jvmName
    )

    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            Categories,
            Classrooms,
            ClassroomsEquipments,
            Comments,
            Equipments,
            Inventories,
            Users
        )
    }
}
