package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.InventoryManager
import com.ksenialexeev.models.CreateInventoryDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.inventoryRouting() {
    val inventoryManager by inject<InventoryManager>()

    route("inventory") {
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            get {
                call.respond(inventoryManager.getAll())
            }
            delete("{id}") {
                call.parameters["id"]?.let { inventoryManager.delete(it.toInt()) }
                    ?.let { call.respondText("Inventory with Id ${call.parameters["id"]} deleted") }
            }
            post {
                val inventoryData = call.receive<CreateInventoryDto>()
                 call.respond(inventoryManager.create(inventoryData)!!)
            }
            get("not-in-inventory"){
                call.respond(inventoryManager.notInInventory())
            }
        }
    }
}