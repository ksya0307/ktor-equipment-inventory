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
            get {
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
                        }
                    }
                )
            }
            authenticate("auth-jwt-admin"){
                delete("{id}") {
                    call.parameters["id"]?.let { it1 -> categoryManager.delete(it1.toInt()) }
                        ?.let { it2 -> call.respond(it2) }
                }
            }
        }

}