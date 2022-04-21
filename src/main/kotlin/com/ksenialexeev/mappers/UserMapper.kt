package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.User
import com.ksenialexeev.models.CreateUserDto
import com.ksenialexeev.models.UserDto
import com.ksenialexeev.models.UserLoginDto

class UserMapper {
    operator fun invoke(user: User) = UserDto(
        id = user.id.value,
        surname = user.surname,
        name = user.name,
        patronymic = user.patronymic,
        username = user.username,
        password = user.password,
        role = user.role
    )
}

class UserLoginMapper{
    operator fun invoke(user: User) = UserLoginDto(
        username = user.username,
        password = user.password
    )
}

class CreateUserMapper {
    operator fun invoke(user: User) = CreateUserDto(
        surname = user.surname,
        name = user.name,
        patronymic = user.patronymic,
        username = user.username,
        password = user.password
    )
}