package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.RepairEquipmentManager
import com.ksenialexeev.database.managers.RepairManager
import com.ksenialexeev.models.CreateRepairEquipmentDto
import com.ksenialexeev.models.UpdateRepairDto
import com.ksenialexeev.models.UpdateRepairEquipmentDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
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
            get("user-repair") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                call.respond(repairEquipmentManager.getByUserId(userId))

            }
            post {
                var repairData = call.receive<CreateRepairEquipmentDto>()
                repairEquipmentManager.create(repairData)
                call.respondText("Repair Equipment with repair Id ${repairData.repair_id} created")
            }
            delete("{id}") {
                call.parameters["id"]?.let { repairEquipmentManager.delete(it.toInt()) }?.let {
                    call.respondText("Repair Document ${call.parameters["id"]} deleted")
                }
            }
            put("{id}") {
                val repairData = call.receive<UpdateRepairEquipmentDto>()
                call.parameters["id"]?.let {
                    repairEquipmentManager.update(
                        repairData.repair_id, repairData.equipment_id,
                        it.toInt(), repairData.problem
                    )
                }
                call.respondText("Repair updated")
            }
        }
    }

    val repairManager by inject<RepairManager>()

    route("repair") {
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            get {
                call.respond(repairManager.getAll())
            }

        }

        authenticate("auth-jwt-moderator", "auth-jwt-admin", "auth-jwt-teacher") {
            post {
                call.respond(repairManager.create(call.receive()))
            }
            put() {
                val repairData = call.receive<UpdateRepairDto>()
                 repairManager.update(repairData.id,repairData.phone,repairData.datetime)

                call.respondText("Repair updated")
            }
            get("{id}") {
                call.parameters["id"]?.let { repairManager.getById(it.toInt()) }
                    ?.let { call.respond(it) }
            }
            delete("{id}") {
                call.parameters["id"]?.let { repairManager.delete(it.toInt()) }?.let {
                    call.respondText("Repair with Id ${call.parameters["id"]} deleted")
                }
            }
        }
    }

}