package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.EquipmentManager
import com.ksenialexeev.models.CreateEquipmentDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.equipmentRouting() {
    val equipmentManager by inject<EquipmentManager>()
    route("equipment") {
        authenticate("auth-jwt-reader", "auth-jwt-moderator", "auth-jwt-admin") {
            get {
                call.respond(equipmentManager.getAll())
            }
            get("{id}") {
                call.parameters["id"]?.let { it1 -> equipmentManager.getById(it1.toInt()) }
                    ?.let { it2 -> call.respond(it2) }
            }
        }
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            post {
                val newEquipment = call.receive<CreateEquipmentDto>()
                val equipment = equipmentManager.create(newEquipment)
                call.respondText("Equipment been created ${newEquipment.description}, ${newEquipment.category.name}")
            }
            delete("{id}") {
                call.parameters["id"]?.let { equipmentManager.delete(it.toInt()) }
                    ?.let { call.respondText("Equipment with Id ${call.parameters["id"]} deleted") }

            }
        }
    }
}