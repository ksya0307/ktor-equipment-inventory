package com.ksenialexeev.routes

import com.ksenialexeev.database.managers.CategoryManager

import com.ksenialexeev.models.CreateOrUpdateCategoryDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
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
        authenticate("auth-jwt-moderator", "auth-jwt-teacher", "auth-jwt-admin") {
            get {
                call.respond( categoryManager.getAll())
            }
        }
        authenticate("auth-jwt-moderator", "auth-jwt-admin") {
            post{
                val categoryData = call.receive<CreateOrUpdateCategoryDto>()
                categoryManager.create(categoryData)
                call.respondText("Category with name ${categoryData.name} created")
            }
            delete("{id}") {
                call.parameters["id"]?.let { categoryManager.delete(it.toInt()) }
                    ?.let { call.respondText("Category with Id ${call.parameters["id"]} deleted") }

            }
            put("{id}"){
                val categoryData = call.receive<CreateOrUpdateCategoryDto>()
                call.parameters["id"]?.let { categoryManager.update(it.toInt(), categoryData.name) }
                call.respondText("Category updated")
            }
        }
    }
}