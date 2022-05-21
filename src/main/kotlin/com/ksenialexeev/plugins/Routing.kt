package com.ksenialexeev.plugins

import com.auth0.jwt.JWT
import com.ksenialexeev.*
import com.ksenialexeev.database.managers.CategoryManager
import com.ksenialexeev.database.managers.UserManager
import com.ksenialexeev.models.TokenPair
import com.ksenialexeev.models.UserLoginDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.SortOrder
import org.koin.ktor.ext.inject
import java.util.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
