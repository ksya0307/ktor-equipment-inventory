package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.ClassroomEquipmentManager
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.classroomEquipment() {
    val classroomEquipmentManager by inject<ClassroomEquipmentManager>()
    route("classroom-equipment") {
        get {
            call.respond(
                classroomEquipmentManager.getAllClassroomEquipment(
                    call.request.queryParameters["classroom"],
                    call.request.queryParameters["equipment"]
                )
            )
        }
    }
}