package com.ksenialexeev.database.managers

interface UserManager {
    suspend fun login(username:String, password:String)
}