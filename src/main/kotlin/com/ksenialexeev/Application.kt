package com.ksenialexeev

import com.ksenialexeev.database.initDatabase
import com.ksenialexeev.exceptions.NotFoundException
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.ksenialexeev.plugins.*
import com.ksenialexeev.routes.categoryRouting
import com.ksenialexeev.routes.classroomRouting
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.json
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun main() {

    initDatabase()

    //Heroku uses the PORT environment variable
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {

        val json by inject<Json>()

        install(Koin) {
            modules(jsonModule, mappersModule, managerModule)
        }
        //install(Resources)
        install(ContentNegotiation) {
            json(json)
        }

        install(StatusPages) {
            exception<Exception> {
                call.respond(HttpStatusCode.BadRequest, it.message.orEmpty())
            }
            exception<NotFoundException>{
                call.respond(HttpStatusCode.BadRequest, it.message.orEmpty())
            }
        }

        routing{
            route("api/v1/"){
                categoryRouting()
                classroomRouting()
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

