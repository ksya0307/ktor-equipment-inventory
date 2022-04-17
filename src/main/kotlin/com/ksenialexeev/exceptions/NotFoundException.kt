package com.ksenialexeev.exceptions

class NotFoundException(
    val name: String,
    val id: Any
) : Exception(
    "$name with id $id not found"
)