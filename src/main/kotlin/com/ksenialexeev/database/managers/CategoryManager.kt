package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Categories
import com.ksenialexeev.database.tables.Category
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.CategoryMapper
import com.ksenialexeev.models.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


interface CategoryManager {
    suspend fun create(dto: CreateOrUpdateCategoryDto): CategoryDto
    suspend fun delete(id: Int): HttpStatusCode
    suspend fun getAll(): List<CategoryDto>
    suspend fun update(id:Int, name:String) : CategoryDto
}

class CategoryManagerImpl : CategoryManager, KoinComponent {

    private val mapper by inject<CategoryMapper>()

    override suspend fun create(dto: CreateOrUpdateCategoryDto) = newSuspendedTransaction(Dispatchers.IO) {
        val categoryId = Category.find { Categories.name.lowerCase() eq dto.name.lowercase() }
        print(categoryId)
        if (categoryId.empty()) {
            Category.new {
                name = dto.name
            }.let { mapper(it) }
        } else {
            throw  NotFoundException("Category already exists", dto.name)
        }
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Category.findById(id)?.let { it.delete(); HttpStatusCode.OK } ?: throw NotFoundException("Category", id)
    }

    override suspend fun getAll(): List<CategoryDto> = newSuspendedTransaction(Dispatchers.IO) {
        Category.all().map(mapper::invoke)
    }

    override suspend fun update(id: Int, name: String)= newSuspendedTransaction(Dispatchers.IO) {
        val category = Category.find {Categories.name eq name}
        if(category.empty()){
            Category.findById(id)?.let {
                it.name = name
                mapper(it)
            } ?: throw NotFoundException("Category", id)
        }else {
            throw NotFoundException("Category already exists", name)
        }
    }
}