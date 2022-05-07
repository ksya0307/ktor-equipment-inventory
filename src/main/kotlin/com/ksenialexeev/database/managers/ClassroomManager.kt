package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Classroom
import com.ksenialexeev.database.tables.Classrooms
import com.ksenialexeev.database.tables.User
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.ClassroomMapper
import com.ksenialexeev.models.ClassroomDto
import com.ksenialexeev.models.CreateClassroomDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ClassroomManager {
    suspend fun getAllClassrooms(): List<ClassroomDto>
    suspend fun getClassroomsByUser(id: Int): List<ClassroomDto>
    suspend fun deleteByNumber(number: String): HttpStatusCode
    suspend fun create(dto: CreateClassroomDto): ClassroomDto
}

class ClassroomManagerImpl : ClassroomManager, KoinComponent {

    private val mapper by inject<ClassroomMapper>()

    override suspend fun getAllClassrooms() = newSuspendedTransaction(Dispatchers.IO) {
        Classroom.all().map(mapper::invoke)
    }

    override suspend fun getClassroomsByUser(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        val user: User = User.findById(id) ?: throw NotFoundException("User", id)
        Classroom.find {
            Classrooms.user eq user.id
        }.map(mapper::invoke)
    }

    override suspend fun deleteByNumber(number: String) = newSuspendedTransaction(Dispatchers.IO) {
        Classroom.findById(number)?.let { it.delete();HttpStatusCode.OK } ?: throw NotFoundException(
            "Classroom",
            number
        )
    }

    override suspend fun create(dto: CreateClassroomDto) = newSuspendedTransaction(Dispatchers.IO) {
        val existingClassroom = Classroom.findById(dto.number)
            if (existingClassroom == null) {

                Classroom.new(dto.number) {
                    user = User.findById(dto.user) ?: throw NotFoundException("User not found", dto.user)
                }.let { mapper(it) }
            } else {
                throw NotFoundException("Classroom already exists", dto.number)
            }
    }

}