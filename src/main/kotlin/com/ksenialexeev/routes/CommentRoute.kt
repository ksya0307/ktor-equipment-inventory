package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.CommentManager
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject


fun Route.commentRouting(){
    val commentManager by inject<CommentManager>()
    route("comments"){
        get{
            call.respond(commentManager.getAllComments())
        }
        get("{inventory-id}"){
            call.parameters["inventory-id"]?.let { it1 -> commentManager.getCommentByInventory(it1.toInt()) }
                ?.let { it2 -> call.respond(it2) }
        }
    }
}