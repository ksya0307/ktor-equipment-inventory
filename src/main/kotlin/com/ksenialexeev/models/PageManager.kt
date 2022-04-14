package com.ksenialexeev.models

import com.ksenialexeev.database.tables.Categories
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SortOrder

abstract class PageManager<Model>(
    open val table: Table,
    open val mapper: OneWayMapper<ResultRow, Model>
) : IPageManger<Model> {
    override suspend fun getPage(
        page: Long,
        size: Int?,
        sortDirection: SortOrder,
        sortByColumn: String
    ): Paging<Model> = suspendedTransactionAsync {
        val column = table.columns.singleOrNull { it.name == sortByColumn } ?: throw Exception("Column not found.")
        println(column.name)
        println(Categories.id.name)
        val query = table
            .selectAll()
            .orderBy(column, sortDirection)
        if (size != null) {
            query.limit(size, page * size)
        }
        query.forEach { println(it[column]) }
        Paging<Model>(
            page, size, sortDirection, sortByColumn
        ).apply {
            sizeResult = query.count()
            result = query.map(mapper::invoke)
        }
    }.await()
}

interface OneWayMapper<in Input, out Output> {
    operator fun invoke(input: Input): Output
}

interface IPageManger<Model> {
    suspend fun getPage(
        page: Long = 0,
        size: Int? = null,
        sortDirection: SortOrder = SortOrder.ASC,
        sortByColumn: String = ""
    ): Paging<Model>
}

@Serializable
data class Paging<T>(
    val page: Long = 0,
    val size: Int? = null,
    val sortDirection: SortOrder = SortOrder.ASC,
    val sortByColumn: String = "",
    var sizeResult: Long? = null,
    var result: List<T>? = null
)