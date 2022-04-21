package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Classroom
import com.ksenialexeev.database.tables.Classrooms.user
import com.ksenialexeev.mappers.UserMapper
import com.ksenialexeev.models.ClassroomDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClassroomMapper : KoinComponent {

    val userMapper by inject<UserMapper>()

    operator fun invoke(classroom: Classroom) = ClassroomDto(
        number = classroom.id.value,
        user = userMapper(user = classroom.user)
    )
}