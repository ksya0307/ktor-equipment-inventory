package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.RepairEquipmentManager
import com.ksenialexeev.database.managers.RepairManager
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.models.CreateRepairEquipmentDto
import com.ksenialexeev.models.RepairDto
import com.ksenialexeev.models.UpdateRepairDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.repairRouting() {
    val repairEquipmentManager by inject<RepairEquipmentManager>()

    route("repair-equipment") {
        authenticate("auth-jwt-moderator", "auth-jwt-teacher", "auth-jwt-admin") {
            get {
                call.respond(repairEquipmentManager.getAll())
            }
            get("{user-id}") {
                call.parameters["user-id"]?.let { repairEquipmentManager.getByUserId(it.toInt()) }?.also { call.respond(it) }
                    ?: throw NotFoundException("Repair not found", "")
            }
            post{
                var repairData = call.receive<CreateRepairEquipmentDto>()
                repairEquipmentManager.create(repairData)
                call.respondText("Repair Equipment with repair Id ${repairData.repair_id} created")
            }
        }
    }

    val repairManager by inject<RepairManager>()

    route("repair") {
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            get {
                call.respond(repairManager.getAll())
            }
            delete("{id}") {
                call.parameters["id"]?.let { repairManager.delete(it.toInt()) }?.let {
                    call.respondText("Repair with Id ${call.parameters["id"]} deleted")
                }
            }
            }

        authenticate("auth-jwt-moderator", "auth-jwt-admin", "auth-jwt-teacher") {
            post {
                call.respond(repairManager.create(call.receive()))
            }
            put("{repair-id}") {
                val repairData = call.receive<UpdateRepairDto>()
                call.parameters["repair-id"]?.let { it -> repairManager.update(it.toInt(), repairData.phone, repairData.datetime) }
                call.respondText("Repair updated")
            }
            get("{id}") {
                call.parameters["id"]?.let { repairManager.getById(it.toInt()) }
                    ?.let { call.respond(it) }
            }

        }
        }

}