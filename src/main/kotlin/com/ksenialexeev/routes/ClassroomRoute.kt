package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.ClassroomManager
import com.ksenialexeev.models.ClassroomDto
import com.ksenialexeev.models.CreateOrUpdateClassroomDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.classroomRouting() {
    val classroomManager by inject<ClassroomManager>()
    route("classrooms") {
        authenticate("auth-jwt-moderator", "auth-jwt-teacher", "auth-jwt-admin") {
            get {
                call.respond(classroomManager.getAllClassrooms())
            }
            get("user-classrooms") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                call.respond(classroomManager.getClassroomsByUser(userId))
            }
        }
        authenticate("auth-jwt-admin") {
            delete("{classroom-number}") {
                classroomManager.deleteByNumber(call.parameters["classroom-number"].toString()).let{
                    call.respondText("Classroom with Number ${call.parameters["classroom-number"]} deleted")
                }
            }
            post{
                val classroomData = call.receive<CreateOrUpdateClassroomDto>()
                classroomManager.create(classroomData).let { call.respondText("Classroom ${classroomData.number} created") }
            }
        }
    }
}