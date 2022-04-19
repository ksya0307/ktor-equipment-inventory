package com.ksenialexeev.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ksenialexeev.*
import com.ksenialexeev.database.managers.UserManager
import com.ksenialexeev.models.TokenPair
import com.ksenialexeev.models.UserLoginDto
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import io.ktor.response.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import java.util.*

fun Route.userRouting(){
    val userManager by inject<UserManager>()

    route("login"){
        post {
            val user = call.receive<UserLoginDto>()
            println("$user.username $user.password")
            val access = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("id", userManager.login(user.username, user.password))
                .withExpiresAt(Date(System.currentTimeMillis() + accessTokenPeriod))
                .sign(Algorithm.HMAC256(System.getenv("SECRET") ?: "secret"))
            val refresh = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("id", userManager.login(user.username, user.password))
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenPeriod))
                .sign(Algorithm.HMAC256(System.getenv("SECRET") ?: "secret"))
            call.respond(TokenPair(access, refresh))
        }

    }

    authenticate("auth-jwt-refresh") {
        get("refresh") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("id").asInt()
            val access = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("id", userId)
                .withExpiresAt(Date(System.currentTimeMillis() + accessTokenPeriod))
                .sign(Algorithm.HMAC256(System.getenv("SECRET") ?: "secret"))
            val refresh = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("id", userId)
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenPeriod))
                .sign(Algorithm.HMAC256(System.getenv("SECRET") ?: "secret"))
            call.respond(TokenPair(access, refresh))
        }
    }

}