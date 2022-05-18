package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.DocumentManager
import com.ksenialexeev.models.ChangeDocumentDto
import com.ksenialexeev.models.DocumentDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.documentRouting(){
    val documentManager by inject<DocumentManager>()

    route("documents"){
        authenticate ( "auth-jwt-moderator", "auth-jwt-admin"){
            get {
                call.respond(documentManager.getAll())
            }
            post {
                val documentData = call.receive<DocumentDto>()
                documentManager.create(documentData)
                call.respondText("Document ${documentData.name} created")
            }
            put {
                val documentData = call.receive<ChangeDocumentDto>()
                documentManager.update(documentData)
                call.respondText("Document updated")
            }
            delete("{id}") {
                call.parameters["id"]?.let {  documentManager.delete(it.toInt()) }.let {
                    call.respondText("Document deleted")
                }
            }
        }
    }
}