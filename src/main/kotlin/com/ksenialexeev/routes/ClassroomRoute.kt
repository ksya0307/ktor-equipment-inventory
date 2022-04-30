package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.ClassroomManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.classroomRouting() {
    val classroomManager by inject<ClassroomManager>()
    route("classrooms") {
        authenticate("auth-jwt-moderator", "auth-jwt-reader", "auth-jwt-admin") {
            get {
                call.respond(classroomManager.getAllClassrooms())
            }
            get("{user-id}") {
                call.parameters["user-id"]?.let { it1 -> classroomManager.getClassroomsByUser(it1.toInt()) }
                    ?.let { it2 -> call.respond(it2) }
            }
        }
    }
}