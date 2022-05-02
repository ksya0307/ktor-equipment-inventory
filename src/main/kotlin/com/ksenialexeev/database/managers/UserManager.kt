package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Role
import com.ksenialexeev.database.tables.User
import com.ksenialexeev.database.tables.Users
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.UserLoginMapper
import com.ksenialexeev.mappers.UserMapper
import com.ksenialexeev.models.CreateUserDto
import com.ksenialexeev.models.UserDto
import com.ksenialexeev.models.UserLoginDto
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt


interface UserManager {
    suspend fun login(username: String, password: String): Int
    suspend fun check(id: Int): Boolean
    suspend fun signup(dto: CreateUserDto): UserLoginDto
    suspend fun checkModerator(id: Int): Boolean
    suspend fun checkReader(id: Int): Boolean
    suspend fun checkAdmin(id:Int):Boolean
    suspend fun checkCommon(id: Int): Boolean
    suspend fun getUser(id: Int): UserDto
    suspend fun changeRole(id: Int, role: Role?, surname: String?, name:String?, patronymic:String?): UserDto
}

class UserManagerImpl : UserManager, KoinComponent {

    //для возврата
    private val mapper by inject<UserLoginMapper>()

    private val mapperGetUser by inject<UserMapper>()

    private fun encryptPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(12))

    private fun Bcrypt.verify(loginPassword: String, userHashedPassword: String): Boolean =
        verify(loginPassword, userHashedPassword.toByteArray())


    override suspend fun login(username: String, password: String) = newSuspendedTransaction(Dispatchers.IO) {
        User.find {
            (Users.username eq username)
        }.firstOrNull { Bcrypt.verify(password, it.password) }?.id?.value ?: throw Exception()
    }

    override suspend fun check(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { true } ?: false
    }

    override suspend fun signup(dto: CreateUserDto) = newSuspendedTransaction(Dispatchers.IO) {
        User.new {
            surname = dto.surname
            name = dto.name
            patronymic = dto.patronymic
            username = dto.username
            password = encryptPassword(dto.password)
        }.let(mapper::invoke)
    }

    override suspend fun checkModerator(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.moderator } ?: false
    }

    override suspend fun checkAdmin(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.admin } ?: false
    }

    override suspend fun checkReader(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.reader } ?: false
    }
    override suspend fun checkCommon(id: Int): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.common } ?: false
    }

    override suspend fun getUser(id: Int): UserDto  = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let{  mapperGetUser(it) } ?: throw NotFoundException("User", id)
    }

    override suspend fun changeRole(id: Int, role: Role?, surname: String?, name:String?, patronymic:String?): UserDto =  newSuspendedTransaction(Dispatchers.IO)  {
        User.findById(id)?.let {
            if (role != null) {
                it.role = role
            }
            if (surname != null) {
                it.surname = surname
            }
            if (name != null) {
                it.name = name
            }
            if (patronymic != null) {
                it.patronymic = patronymic
            }
            mapperGetUser(it)
        } ?: throw NotFoundException("Changes of User", id)
    }

}