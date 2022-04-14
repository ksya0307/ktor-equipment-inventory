package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.ClassroomManager
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.classroomRouting() {
    val classroomManager by inject<ClassroomManager>()

    route("classrooms") {
        get{
            call.respond(classroomManager.getAllClassrooms())
        }
    }
}