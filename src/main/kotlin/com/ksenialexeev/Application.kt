package com.ksenialexeev

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.ksenialexeev.plugins.*

fun main() {
    //Heroku uses the PORT environment variable

    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        configureRouting()
     //   configureSerialization()
     //   configureSecurity()
    }.start(wait = true)
}
//Procfile
//Этот файл указывает путь к исполняемому файлу приложения,
// создаваемому задачей stage, и позволяет Heroku запустить приложение.