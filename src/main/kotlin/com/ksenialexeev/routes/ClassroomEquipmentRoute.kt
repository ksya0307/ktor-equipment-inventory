package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.ClassroomEquipmentManager
import com.ksenialexeev.models.CreateClassroomEquipmentDto
import com.ksenialexeev.models.UpdateClassroomEquipmentDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.classroomEquipment() {
    val classroomEquipmentManager by inject<ClassroomEquipmentManager>()
    route("classroom-equipment") {
        authenticate("auth-jwt-moderator", "auth-jwt-reader", "auth-jwt-admin") {
            get {
                call.respond(
                    classroomEquipmentManager.getAllClassroomEquipmentByClassroomAndCategory(
                        call.request.queryParameters["classroom"], call.request.queryParameters["equipment"]
                    )
                )
            }
            get("{id}"){
                call.parameters["id"]?.let { it1 -> classroomEquipmentManager.getSpecsById(it1.toInt()) }
                    ?.let { it2 -> call.respond(it2) }
            }
        }
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            post {
                val newClEq = call.receive<CreateClassroomEquipmentDto>()
                println("${newClEq.classroom} ${newClEq.number_in_classroom}")
                val clEq = classroomEquipmentManager.create(newClEq)

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