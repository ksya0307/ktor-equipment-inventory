package com.ksenialexeev.exceptions

class NotFoundException(
    val name: String,
    val id: Int
) : Exception(
    "$name with id $id not found"
)