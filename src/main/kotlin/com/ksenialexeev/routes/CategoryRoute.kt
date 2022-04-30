package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.CategoryManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.SortOrder
import org.koin.ktor.ext.inject

fun Route.categoryRouting() {
    val categoryManager by inject<CategoryManager>()
    route("categories") {
        authenticate("auth-jwt-moderator", "auth-jwt-reader", "auth-jwt-admin") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respond(
                    call.request.queryParameters.let { params ->
                        println(params.names().map { it to params[it] })
                        if (params.contains("size")) {
                            categoryManager.getPage(
                                page = params["page"]?.toLong() ?: throw Exception("Missing page"),
                                size = params["size"]?.toInt() ?: throw Exception("Missing size"),
                                sortDirection = params["sortDirection"]?.let { SortOrder.valueOf(it) }
                                    ?: throw Exception("Missing/invalid sortDirection"),
                                sortByColumn = params["sortByColumn"] ?: throw Exception("Missing/invalid sortByColumn")
                            )
                        } else {
                            categoryManager.getAll()
                            call.respondText("Hello, $userId, Token is expired at $expiresAt ms")
                        }
                    }
                )
            }
        }
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            delete("{id}") {
                call.parameters["id"]?.let { it1 -> categoryManager.delete(it1.toInt()) }
                    ?.let { it2 -> call.respond(it2) }
            }
        }
    }
}