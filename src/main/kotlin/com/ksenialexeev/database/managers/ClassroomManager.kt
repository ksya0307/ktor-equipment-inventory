package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Classroom
import com.ksenialexeev.database.tables.Classrooms
import com.ksenialexeev.database.tables.User
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.ClassroomMapper
import com.ksenialexeev.models.ClassroomDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ClassroomManager {
    suspend fun getAllClassrooms(): List<ClassroomDto>
    suspend fun getClassroomsByUser(id: Int): List<ClassroomDto>
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

}