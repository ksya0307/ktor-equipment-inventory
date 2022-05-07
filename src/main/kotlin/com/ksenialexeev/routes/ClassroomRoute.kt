package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.ClassroomManager
import com.ksenialexeev.models.ClassroomDto
import com.ksenialexeev.models.CreateClassroomDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
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
        authenticate("auth-jwt-admin") {
            delete("{classroom-number}") {
                classroomManager.deleteByNumber(call.parameters["classroom-number"].toString()).let{
                    call.respondText("Classroom with Number ${call.parameters["classroom-number"]} deleted")
                }
            }
            post{
                val classroomData = call.receive<CreateClassroomDto>()
                classroomManager.create(classroomData).let { call.respondText("Classroom ${classroomData.number} created") }
            }
        }
    }
}