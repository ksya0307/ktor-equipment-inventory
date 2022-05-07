package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.CategoryManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SortOrder
import org.koin.ktor.ext.inject
import kotlinx.serialization.encodeToString

fun Route.categoryRouting() {
    val categoryManager by inject<CategoryManager>()
    val json by inject<Json>()
    route("categories") {
        authenticate("auth-jwt-moderator", "auth-jwt-reader", "auth-jwt-admin") {
            get {
                call.respondText(
                    call.request.queryParameters.let { params ->
                        println(params.names().map { it to params[it] })
                        if (params.contains("size")) {
                            categoryManager.getPage(
                                page = params["page"]?.toLong() ?: 0,
                                size = params["size"]?.toInt() ?: 10,
                                sortDirection = params["sortDirection"]?.let { SortOrder.valueOf(it) }
                                    ?: SortOrder.ASC,
                                sortByColumn = params["sortByColumn"]
                            )
                        } else {
                            json.encodeToString(categoryManager.getAll())
                        }
                    }, contentType = ContentType.Application.Json
                )
            }
        }
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            delete("{id}") {
                call.parameters["id"]?.let { categoryManager.delete(it.toInt()) }
                    ?.let { call.respondText("Category with Id ${call.parameters["id"]} deleted") }

            }
        }
    }
}