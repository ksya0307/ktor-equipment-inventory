package com.ksenialexeev.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table


abstract class PageManager<Model : Any>(
    open val table: Table,
    open val mapper: OneWayMapper<ResultRow, Model>
) : IPageManger<Model> {
    /*override suspend fun getPage(
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
        //query.forEach { println(it[column]) }
        Paging(
            page, size ?: 0, sortDirection, sortByColumn,
            query.count(),
            query.map(mapper::invoke)
        ).also {
            //Json.encodeToString(Paging<Model>::class.serializer(), it).let(::println)
        }
    }.await()*/
}

interface OneWayMapper<in Input, out Output> {
    operator fun invoke(input: Input): Output
}

interface IPageManger<Model> {
    suspend fun getPage(
        page: Long = 0,
        size: Int? = null,
        sortDirection: SortOrder = SortOrder.ASC,
        sortByColumn: String? = null
    ): String
}

class SortOrderSerializer() : KSerializer<SortOrder> {
    override fun deserialize(decoder: Decoder): SortOrder {
        return SortOrder.valueOf(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: SortOrder) {
        encoder.encodeString(value.name)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SortOrder", PrimitiveKind.STRING)

}


@Serializable
data class Paging<T: Any>(
    val page: Long = 0,
    val size: Int = 0,
    @Serializable(with = SortOrderSerializer::class)
    val sortDirection: SortOrder = SortOrder.ASC,
    val sortByColumn: String? = null,
    val sizeResult: Long = 0L,
    val result: List<T> = emptyList()
)