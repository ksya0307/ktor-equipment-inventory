package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.User
import com.ksenialexeev.models.UserDto

class UserMapper {
    operator fun invoke(user: User) = UserDto(
        id = user.id.value,
        surname = user.surname,
        name = user.name,
        patronymic = user.patronymic
    )
}