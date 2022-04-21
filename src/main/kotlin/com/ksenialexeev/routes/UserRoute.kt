package com.ksenialexeev.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ksenialexeev.*
import com.ksenialexeev.database.managers.UserManager
import com.ksenialexeev.models.CreateUserDto
import com.ksenialexeev.models.TokenPair
import com.ksenialexeev.models.UserLoginDto
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import io.ktor.response.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import java.util.*

fun Route.userRouting() {
    val userManager by inject<UserManager>()

    route("users") {
        authenticate("auth-jwt-admin", "auth-jwt-reader") {
            get("{id}") {
                call.parameters["id"]?.toInt()?.let { it1 -> userManager.getUser(it1) }
                    ?.let { it2 -> call.respond(it2) }
            }
        }
    }

    route("sign_up") {
        post {
            val newUser = call.receive<CreateUserDto>()
            val user = userManager.signup(newUser)
            call.respondText(
                "User been created ${newUser.surname}, ${newUser.name}, ${newUser.patronymic}," +
                        " ${newUser.username}, ${newUser.password}"
            )
        }
    }
    route("login") {
        post {
            val authUser = call.receive<UserLoginDto>()
            val user = userManager.login(authUser.username, authUser.password)
            println("$user id")
            val access = JWT.create().withAudience(audience).withIssuer(issuer)
                .withClaim("id", userManager.login(authUser.username, authUser.password))
                .withExpiresAt(Date(System.currentTimeMillis() + accessTokenPeriod))
                .sign(algorithm)
            println("$access что тут")
            val refresh = JWT.create().withAudience(audience).withIssuer(issuer)
                .withClaim("id", userManager.login(authUser.username, authUser.password))
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenPeriod))
                .sign(algorithm)
            call.respond(TokenPair(access, refresh))

        }

    }

    authenticate("auth-jwt-refresh") {
        get("refresh") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("id").asInt()
            val access = JWT.create().withAudience(audience).withIssuer(issuer).withClaim("id", userId)
                .withExpiresAt(Date(System.currentTimeMillis() + accessTokenPeriod))
                .sign(algorithm)
            val refresh = JWT.create().withAudience(audience).withIssuer(issuer).withClaim("id", userId)
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenPeriod))
                .sign(algorithm)
            call.respond(TokenPair(access, refresh))
        }
    }

}