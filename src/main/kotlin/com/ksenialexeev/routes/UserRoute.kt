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
        get("existing-user/{id}"){
            call.parameters["id"]?.let {  userManager.existingUser(it.toInt()) }
                ?.let { call.respondText("User with ${call.parameters["id"]} exists") }

        }
        authenticate("auth-jwt-admin") {
            get("all"){
                call.respond(userManager.allUsers())
            }
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
            put("change-password/{user-id}") {
                val userData = call.receive<ChangePasswordDto>()
                call.parameters["user-id"]?.let { it -> userManager.changePassword(it.toInt(), userData.password) }
                call.respondText("Password Changed")
            }
        }

        authenticate("auth-jwt-moderator", "auth-jwt-teacher", "auth-jwt-admin", "auth-jwt-common") {
            get() {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                call.respond(userManager.getUser(userId))
            }
            put("change-password") {
                val userData = call.receive<ChangePasswordDto>()
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("id").asInt()
                userManager.changePassword(userId, userData.password)
                call.respondText("Password Successfully  Changed")
            }



        }
    }

    route("sign_up") {
        post {
            val newUser = call.receive<CreateUserDto>()
            userManager.signup(newUser)
            call.respondText(
                "User been created ${newUser.surname}, ${newUser.name}, ${newUser.patronymic}," +
                        " ${newUser.username}, ${newUser.role}"
            )
        }
    }
    route("login") {
        post {
            val authUser = call.receive<UserLoginDto>()
            val user = userManager.login(authUser.username, authUser.password)
            val access = JWT.create().withAudience(audience).withIssuer(issuer)
                .withClaim("id", user)
                .withExpiresAt(Date(System.currentTimeMillis() + accessTokenPeriod))
                .sign(algorithm)
            val refresh = JWT.create().withAudience(audience).withIssuer(issuer)
                .withClaim("id", user)
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenPeriod))
                .sign(algorithm)
            println(System.currentTimeMillis() + refreshTokenPeriod)
            println(refreshTokenPeriod)
            println(System.currentTimeMillis())
            call.respond(TokenPair(access, refresh,System.currentTimeMillis() + accessTokenPeriod, System.currentTimeMillis() + refreshTokenPeriod ))

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
            call.respond(TokenPair(access, refresh,System.currentTimeMillis() + accessTokenPeriod, System.currentTimeMillis() + refreshTokenPeriod))
        }
    }

}