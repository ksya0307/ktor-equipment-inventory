package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Role
import com.ksenialexeev.database.tables.User
import com.ksenialexeev.database.tables.Users
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.user.UserLoginMapper
import com.toxicbakery.bcrypt.Bcrypt
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface UserManager {
    suspend fun login(username: String, password: String): Int
    suspend fun check(id: Int): Boolean
//    suspend fun signup(username: String, password: String)
    suspend fun checkAdmin(id: Int): Boolean
    suspend fun checkReader(id: Int): Boolean
}

class UserManagerImpl : UserManager, KoinComponent {

    private val mapper by inject<UserLoginMapper>()

    private fun encryptPassword(password: String) = Bcrypt.hash(password, 31).toString()

    override suspend fun login(username: String, password: String) = newSuspendedTransaction(Dispatchers.IO) {
        User.find {
                (Users.username eq username) and (Users.password eq encryptPassword(password))
            }.firstOrNull()?.id?.value ?: throw NotFoundException("User", "$username $password")
    }

    override suspend fun check(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { true } ?: false
    }

    override suspend fun checkAdmin(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.ADMIN } ?: false
    }

    override suspend fun checkReader(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.READER } ?: false
    }
}