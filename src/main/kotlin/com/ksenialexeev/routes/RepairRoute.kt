package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.RepairEquipmentManager
import com.ksenialexeev.database.managers.RepairManager
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
        authenticate("auth-jwt-moderator", "auth-jwt-reader", "auth-jwt-admin") {
            get {
                call.respond(repairEquipmentManager.getAll())
            }

        }
    }

    val repairManager by inject<RepairManager>()

    route("repair") {
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            get {
                call.respond(repairManager.getAll())
            }
            get("{id}") {
                call.parameters["id"]?.let { repairManager.getById(it.toInt()) }
                    ?.let { call.respond(it) }
            }
            post {
                val repairData = call.receive<RepairDto>()
                repairManager.create(repairData)
                call.respondText("Repair created")
            }
            put {
                val repairData = call.receive<UpdateRepairDto>()
                repairManager.update(repairData.id, repairData.phone, repairData.datetime)
                call.respondText("Repair with Id ${repairData.id} updated")
            }
            delete("{id}"){
                call.parameters["id"]?.let { repairManager.delete(it.toInt()) }?.let {
                    call.respondText("Repair with Id ${call.parameters["id"]} deleted")
                }
            }
        }
    }
}