package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.CommentManager
import com.ksenialexeev.models.CreateCommentDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject


fun Route.commentRouting() {
    val commentManager by inject<CommentManager>()
    route("comments") {
        authenticate("auth-jwt-moderator", "auth-jwt-teacher", "auth-jwt-admin") {
            get {
                call.respond(commentManager.getAllComments())
            }
            get("{inventory-id}") {
                call.parameters["inventory-id"]?.let { it1 -> commentManager.getCommentByInventory(it1.toInt()) }
                    ?.let { it2 -> call.respond(it2) }
            }
        }
        authenticate("auth-jwt-moderator","auth-jwt-admin"){
            delete("{id}"){
                call.parameters["id"]?.let { commentManager.delete(it.toInt()) }
                    ?.let { call.respondText("Comment with id ${call.parameters["id"]} deleted") }

            }
            post{
                val commentData = call.receive<CreateCommentDto>()
                commentManager.create(commentData)
                call.respondText("Comment create at ${commentData.datetime}")
            }
        }
    }
}