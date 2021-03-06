package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Category
import com.ksenialexeev.models.CategoryDto

class CategoryMapper {
    operator fun invoke(category:Category) = CategoryDto(
        id = category.id.value,
        name = category.name,
    )
}