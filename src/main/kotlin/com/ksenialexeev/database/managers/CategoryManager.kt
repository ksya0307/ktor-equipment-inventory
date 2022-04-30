package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Categories
import com.ksenialexeev.database.tables.Category
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.CategoryMapper
import com.ksenialexeev.models.*
import io.ktor.http.*
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class CategoryManager : PageManager<CategoryDto>(Categories, CategoryRowMapper) {
    abstract suspend fun create(dto: CreateCategoryDto): CategoryDto
    abstract suspend fun delete(id: Int): HttpStatusCode
    abstract suspend fun getAll(): List<CategoryDto>

}

object CategoryRowMapper : OneWayMapper<ResultRow, CategoryDto> {
    override fun invoke(input: ResultRow): CategoryDto {
       return CategoryDto(name = input[Categories.name])
    }
}

class CategoryManagerImpl : CategoryManager(), KoinComponent {

    private val mapperImpl by inject<CategoryMapper>()

    override suspend fun create(dto: CreateCategoryDto) = newSuspendedTransaction(Dispatchers.IO) {
        Category.new {
            name = dto.name
        }.let { mapperImpl(it) }
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
            Category.findById(id)?.let { it.delete(); HttpStatusCode.OK } ?: throw NotFoundException("Category", id)
    }

    override suspend fun getAll(): List<CategoryDto> = newSuspendedTransaction(Dispatchers.IO) {
        Category.all().map(mapperImpl::invoke)
    }
}