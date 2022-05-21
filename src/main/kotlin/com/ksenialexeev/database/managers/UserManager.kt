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
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt


interface UserManager {
    suspend fun login(username: String, password: String): Int
    suspend fun check(id: Int): Boolean
    suspend fun signup(dto: CreateUserDto): UserLoginDto
    suspend fun checkModerator(id: Int): Boolean
    suspend fun checkTeacher(id: Int): Boolean
    suspend fun checkAdmin(id: Int): Boolean
    suspend fun checkCommon(id: Int): Boolean
    suspend fun getUser(id: Int): UserDto
    suspend fun allUsers():List<UserDto>
    suspend fun changeUser(
        id: Int,
        role: Role?,
        surname: String?,
        name: String?,
        patronymic: String?,
        username: String?,
        password: String?
    ): UserDto
    suspend fun existingUser(id:Int): HttpStatusCode
    suspend fun changePassword(id: Int, password: String): UserDto
    suspend fun delete(id: Int): HttpStatusCode
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

    override suspend fun check(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { true } ?: false
    }

    override suspend fun signup(dto: CreateUserDto) = newSuspendedTransaction(Dispatchers.IO) {
        val userId = User.find { Users.username eq dto.username }

        if (userId.empty()) {
            User.new {
                surname = dto.surname
                name = dto.name
                if (dto.patronymic != null) {
                    patronymic = dto.patronymic
                }
                username = dto.username
                password = encryptPassword(dto.password)
            }.let { mapper(it) }
        } else {
            throw NotFoundException("User already exists", dto.username)
        }

    }

    override suspend fun checkModerator(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.moderator } ?: false
    }

    override suspend fun checkAdmin(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.admin } ?: false
    }

    override suspend fun checkTeacher(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.teacher } ?: false
    }

    override suspend fun checkCommon(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.role == Role.common } ?: false
    }

    override suspend fun getUser(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { mapperGetUser(it) } ?: throw NotFoundException("User", id)
    }

    override suspend fun allUsers()= newSuspendedTransaction(Dispatchers.IO) {
        User.all().map(mapperGetUser::invoke)
    }

    override suspend fun changeUser(
        id: Int,
        role: Role?,
        surname: String?,
        name: String?,
        patronymic: String?,
        username: String?,
        password: String?
    ) =
        newSuspendedTransaction(Dispatchers.IO) {
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
                if (username != null) {
                    it.username = username
                }
                if (password != null) {
                    it.password = encryptPassword(password)
                }
                mapperGetUser(it)
            } ?: throw NotFoundException("Changes of User", id)
        }

    override suspend fun existingUser(id: Int)= newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { HttpStatusCode.OK } ?: throw NotFoundException("User not exist", id)
    }

    override suspend fun changePassword(id: Int, password: String) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let {
            it.password = encryptPassword(password)
            mapperGetUser(it)
        } ?: throw NotFoundException("User with id not changed", id)
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        User.findById(id)?.let { it.delete();HttpStatusCode.OK } ?: throw NotFoundException("User", id)
    }

}