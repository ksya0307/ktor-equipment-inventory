package com.ksenialexeev

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ksenialexeev.database.initDatabase
import com.ksenialexeev.database.managers.UserManager
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.plugins.configureRouting
import com.ksenialexeev.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.datetime.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject


val accessTokenPeriod = (System.getenv("ACCESS_TOKEN_LIFETIME")?.toInt()?.times(60000)) ?: (60000 * 15) //millis
val refreshTokenPeriod = (System.getenv("REFRESH_TOKEN_LIFETIME")?.toInt()?.times((86400000))) ?: ( 86400000  * 5)//millis

val audience = System.getenv("AUDIENCE")?.toString() ?: "audience"
val myRealm = System.getenv("REALM")?.toString() ?: "Access to 'login'"
val issuer = System.getenv("ISSUER")?.toString() ?: "ktor"
val secret = System.getenv("SECRET")?.toString() ?: "equilibrium"
val algorithm: Algorithm = Algorithm.HMAC256(secret)!!

val currentMoment :Instant= Clock.System.now()
val dateTimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())


fun main() {

    initDatabase()

    //Heroku uses the PORT environment variable
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {

        val userManager by inject<UserManager>()

        //authentication provider
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
                    if (userManager.checkAdmin(credential.payload.getClaim("id").asInt())) {
                        //состоит из payload - то ЧТО будет хранить JWT - данные юзера
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
                //Specifies what to send back if jwt authentication fails.
                challenge { defaultScheme, realm->
                    call.respond(HttpStatusCode.Unauthorized, "Access Token is not valid or has expired")
                }
            }
            jwt("auth-jwt-moderator") {
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
                challenge { defaultScheme, realm->
                    call.respond(HttpStatusCode.Unauthorized, "Access Token is not valid or has expired")
                }
            }
            jwt("auth-jwt-teacher") {
                realm = myRealm
                //для верификации токена что            он не expired, что он сгененирован этим сервером
                verifier(
                    JWT.require(algorithm)
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .build()
                )
                validate { credential ->
                    if (userManager.checkTeacher(credential.payload.getClaim("id").asInt())) {
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
            jwt("auth-jwt-common") {
                realm = myRealm
                //для верификации токена что он не expired, что он сгененирован этим сервером
                verifier(
                    JWT.require(algorithm)
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .build()
                )
                validate { credential ->
                    if (userManager.checkCommon(credential.payload.getClaim("id").asInt())) {
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
        }

        val json by inject<Json>()

        install(Koin) {
            modules(jsonModule, mappersModule, managerModule)
        }
        install(ContentNegotiation) {
            json(json)
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
                categoryRouting()
                classroomRouting()
                commentRouting()
                classroomEquipment()
                equipmentRouting()
                inventoryRouting()
                ifoRouting()
                documentRouting()
                repairRouting()
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

