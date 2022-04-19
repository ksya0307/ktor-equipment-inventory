package com.ksenialexeev

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ksenialexeev.database.initDatabase
import com.ksenialexeev.database.managers.UserManager
import com.ksenialexeev.exceptions.NotFoundException
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.ksenialexeev.plugins.*
import com.ksenialexeev.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

val accessTokenPeriod = System.getenv("ACCESS_TOKEN_LIFETIME")?.toInt() ?: 60000
val refreshTokenPeriod = System.getenv("REFRESH_TOKEN_LIFETIME")?.toInt() ?: (60000 * 24 * 3)

val audience = System.getenv("AUDIENCE") ?: "audience"
val myRealm = System.getenv("REALM") ?: "Access to 'login'"
val issuer = System.getenv("ISSUER") ?: "ktor"

fun main() {

    initDatabase()

    //Heroku uses the PORT environment variable
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {

        val json by inject<Json>()

        install(Koin) {
            modules(jsonModule, mappersModule, managerModule)
        }
        install(ContentNegotiation) {
            json(json)
        }
        val secret = System.getenv("SECRET")?.toString() ?: "equilibrium"
        val algorithm = Algorithm.HMAC256(secret)

        val userManager by inject<UserManager>()

        install(Authentication) {
            jwt("auth-jwt-admin") {
                realm = myRealm
                //для верификации токена что он не expired, что он сгененирован этим сервером
                verifier(
                    JWT.require(algorithm)
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .build()
                )
                validate { credential ->
                    if (userManager.checkAdmin(credential.payload.getClaim("id").asInt())) {
                        //состоит из payload - то ЧТО будет хранить JWT - данные юзера
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
                challenge { defaultScheme, realm ->
                    call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
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
                challenge { defaultScheme, realm ->
                    call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
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
                challenge { defaultScheme, realm ->
                    call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
                }
            }
        }

        install(StatusPages) {
            exception<Exception> {
                call.respond(HttpStatusCode.BadRequest, it.message.orEmpty())
            }
            exception<NotFoundException> {
                call.respond(HttpStatusCode.BadRequest, it.message.orEmpty())
            }
        }

        routing {
            route("api/v1/") {
                userRouting()
                authenticate("auth-jwt-reader") {
                    categoryRouting()
                }
                classroomRouting()

                commentRouting()
                classroomEquipment()

            }
        }

        configureRouting()
        //   configureSerialization()
        //   configureSecurity()
    }.start(wait = true)
}
//Procfile
//Этот файл указывает путь к исполняемому файлу приложения,
// создаваемому задачей stage, и позволяет Heroku запустить приложение.

