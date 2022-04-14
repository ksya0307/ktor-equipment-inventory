package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class Pages<T>(
    val page: Int = 0,
    val size: Int = 0,
    val sortBy: String = "",
    val sortDir: SortDir = SortDir.ASC,
    val pages: List<T>
)

@Serializable
enum class SortDir {
    ASC, DESC
}
