package com.ksenialexeev.mappers.user

import com.ksenialexeev.database.tables.User
import com.ksenialexeev.models.UserLoginDto

class UserLoginMapper {
    operator fun invoke(user: User) = UserLoginDto(
        username = user.username,
        password = user.password
    )
}