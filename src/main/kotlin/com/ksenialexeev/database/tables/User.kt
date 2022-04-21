package com.ksenialexeev.database.tables

import org.jetbrains.exposed.crypt.Encryptor
import org.jetbrains.exposed.crypt.encryptedVarchar
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object Users:IntIdTable(){
    val surname = varchar("surname", 64)
    val name = varchar("name",64)
    val patronymic = varchar("patronymic",64)
    val username = varchar("username",64)
    val password = text("password")
    val role = enumeration("role", Role::class).default(Role.READER)
}

class User(id:EntityID<Int>):IntEntity(id){
    companion object: EntityClass<Int, User>(Users)

    var name by Users.name
    var surname by Users.surname
    var patronymic by Users.patronymic
    var username by Users.username
    var password by Users.password
    var role by Users.role
}

enum class Role { ADMIN, READER }