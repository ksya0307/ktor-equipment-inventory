package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.IfoManager
import com.ksenialexeev.models.IfoDto
import com.ksenialexeev.models.UpdateIfoDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.ifoRouting(){
    val ifoManager by inject<IfoManager>()

    route("ifo"){
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            get {
                call.respond(ifoManager.getAll())
            }
            post {
                val ifoData = call.receive<IfoDto>()
                ifoManager.create(ifoData)
                call.respondText("Ifo with name ${ifoData.name} created")
            }
            put {
                val ifoData = call.receive<UpdateIfoDto>()
                ifoManager.update(ifoData.id, ifoData.name)
                call.respondText("Ifo with Id ${ifoData.id} updated")
            }
            delete("{id}") {
                call.parameters["id"]?.let {  ifoManager.delete(it.toInt()) }
                    ?.let { call.respondText("IFO with Id ${call.parameters["id"]} deleted") }
            }
        }
    }
}