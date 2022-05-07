package com.ksenialexeev.routes

import com.auth0.jwt.JWT
import com.ksenialexeev.*
import com.ksenialexeev.database.managers.UserManager
import com.ksenialexeev.models.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.userRouting() {
    val userManager by inject<UserManager>()

    route("users") {
        authenticate("auth-jwt-admin") {
            put {
                val userData = call.receive<ChangeUserDto>()
                userManager.changeUser(
                    userData.id,
                    userData.role,
                    userData.surname,
                    userData.name,
                    userData.patronymic, userData.username, userData.password
                )
                call.respondText("User with ${userData.id} changed")
            }
            delete("{id}") {
                call.parameters["id"]?.let { userManager.delete(it.toInt()) }
                    ?.let { call.respondText("User with Id ${call.parameters["id"]} deleted") }

            }
        }

        authenticate("auth-jwt-moderator", "auth-jwt-reader", "auth-jwt-admin", "auth-jwt-common") {
            get("{id}") {
                call.parameters["id"]?.toInt()?.let { it1 -> userManager.getUser(it1) }
                    ?.let { it2 -> call.respond(it2) }
            }
            put("change-password") {
                val userData = call.receive<ChangePasswordDto>()
                userManager.changePassword(userData.id, userData.password)
                call.respondText("Password Changed")
            }
        }
    }

    route("sign_up") {
        post {
            val newUser = call.receive<CreateUserDto>()
            userManager.signup(newUser)
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
            val access = JWT.create().withAudience(audience).withIssuer(issuer)
                .withClaim("id", userManager.login(authUser.username, authUser.password))
                .withExpiresAt(Date(System.currentTimeMillis() + accessTokenPeriod))
                .sign(algorithm)
            val refresh = JWT.create().withAudience(audience).withIssuer(issuer)
                .withClaim("id", userManager.login(authUser.username, authUser.password))
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenPeriod))
                .sign(algorithm)
            call.respond(TokenPair(access, refresh, user))

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
            call.respond(TokenPair(access, refresh, userId))
        }
    }

}