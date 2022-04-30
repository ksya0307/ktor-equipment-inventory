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

fun Application.categoryRouting() {
    val categoryManager by inject<CategoryManager>()

    routing {
        authenticate("auth-jwt-admin", "auth-jwt-reader") {
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
    }
}

fun Application.userRouting() {
    val userManager by inject<UserManager>()
    install(Authentication) {
        jwt("auth-jwt-admin") {
            realm = myRealm
            //для верификации токена что он не expired, что он сгененирован этим сервером
            verifier(
                JWT
                    .require(algorithm)
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (userManager.checkModerator(credential.payload.getClaim("id").asInt())) {
                    //состоит из payload - то ЧТО будет хранить JWT - данные юзера
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Access Token is not valid or has expired")
            }
        }
        jwt("auth-jwt-reader") {
            realm = myRealm
            //для верификации токена что он не expired, что он сгененирован этим сервером
            verifier(
                JWT.require(algorithm)
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (userManager.checkReader(credential.payload.getClaim("id").asInt())) {
                    //состоит из payload - то ЧТО будет хранить JWT - данные юзера
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Access Token is not valid or has expired")
            }
        }
        jwt("auth-jwt-refresh") {
            realm = myRealm
            //для верификации токена что он не expired, что он сгененирован этим сервером
            verifier(
                JWT.require(algorithm)
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (userManager.check(credential.payload.getClaim("id").asInt())) {
                    //состоит из payload - то ЧТО будет хранить JWT - данные юзера
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Refresh Token is not valid or has expired")
            }
        }

        routing {
            route("users") {
                authenticate("auth-jwt-admin", "auth-jwt-reader") {
                    get("{id}") {
                        call.parameters["id"]?.toInt()?.let { it1 -> userManager.getUser(it1) }
                            ?.let { it2 -> call.respond(it2) }
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
                        call.respond(TokenPair(access, refresh, user))
                    }
                }

            }

        }
    }
}


