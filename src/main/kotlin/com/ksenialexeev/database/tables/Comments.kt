package com.ksenialexeev.database.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Comments: IntIdTable("comments"){
    val inventory = reference(
        "inventory",
        Inventories,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val user = reference(
        "user",
        Users,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val comment = text("comment")
    val datetime = datetime("datetime")
}

class Comment(id:EntityID<Int>) : IntEntity(id){
    companion object: EntityClass<Int, Comment>(Comments)

    var inventory by Inventory referencedOn Comments.inventory
    var user by User referencedOn Comments.user
    var comment by Comments.comment
    var datetime by Comments.datetime
}