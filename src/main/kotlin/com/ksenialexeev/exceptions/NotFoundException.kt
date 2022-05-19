package com.ksenialexeev.exceptions

class NotFoundException(
    val name: String,
    private val detail: Any
) : Exception(
    "$name $detail"
)