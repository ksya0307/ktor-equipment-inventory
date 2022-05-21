package com.ksenialexeev

import com.ksenialexeev.plugins.configureRouting

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }

    }
}
//class CategoryTest {
//
//    @Test
//    fun testCategory() {
//        withTestApplication({ categoryRouting() }) {
//            handleRequest(HttpMethod.Get, "/api/v1/categories").apply {
//                assertEquals(HttpStatusCode.Unauthorized, response.status())
//            }
//        }
//    }
//}
//class UsersTest {
//    @Test
//    fun testUsers() {
//        withTestApplication({ userRouting() }) {
//            handleRequest(HttpMethod.Get, "/api/v1/users").apply {
//                assertEquals(HttpStatusCode.Unauthorized, response.status())
//            }
//        }
//    }
//
//    fun testLogin()  {
//        withTestApplication({ userRouting() }) {
//            with(handleRequest(HttpMethod.Post, "/api/v1/login"){
//                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
//                setBody(listOf("username" to "elena", "password" to "admin").toString())
//            }) {
//                assertEquals( "accessToken" , "sajsgdfgfh")
//                assertEquals( "refreshToken" , "sdsdf")
//            }
//        }
//    }
//}

