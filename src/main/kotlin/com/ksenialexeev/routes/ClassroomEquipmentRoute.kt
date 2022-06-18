package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.ClassroomEquipmentManager
import com.ksenialexeev.models.CreateClassroomEquipmentDto
import com.ksenialexeev.models.UpdateClassroomEquipmentDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.classroomEquipment() {
    val classroomEquipmentManager by inject<ClassroomEquipmentManager>()
    route("classroom-equipment") {
        authenticate("auth-jwt-moderator", "auth-jwt-teacher", "auth-jwt-admin") {
            get {
                call.respond(
                    classroomEquipmentManager.getAllClassroomEquipmentByClassroomAndCategory(
                        call.request.queryParameters["classroom"], call.request.queryParameters["equipment"]
                    )
                )
            }
            get("{id}"){
                call.parameters["id"]?.let { classroomEquipmentManager.getSpecsById(it.toInt()) }
                    ?.let { it1 -> call.respond(it1) }
            }
            get(
                "equipment-in-users-classrooms"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                call.respond(classroomEquipmentManager.getUsersEquipmentInClassrooms(userId))
            }


        }
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            post {
                val newClEq = call.receive<CreateClassroomEquipmentDto>()
                println("ALOOOO ${newClEq.equipment} ${newClEq.number_in_classroom}")
                classroomEquipmentManager.create(newClEq)

                call.respondText("Classroom Equipment created")
            }
            delete("{id}"){
                call.parameters["id"]?.let {classroomEquipmentManager.delete(it.toInt()) }
                    ?.let { call.respondText("ClassroomEquipment with Id ${call.parameters["id"]} deleted") }

            }
            put {
                val updClEq = call.receive<UpdateClassroomEquipmentDto>()
                val clEq = classroomEquipmentManager.update(
                    updClEq.id,
                    updClEq.inventory_number,
                    updClEq.classroom,
                    updClEq.equipment, updClEq.number_in_classroom, updClEq.equipment_type
                )
                call.respondText("ClassroomEquipment with id ${updClEq.id} changed")
            }
        }
    }
}