package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Equipment
import com.ksenialexeev.models.EquipmentDto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EquipmentMapper: KoinComponent {
    val categoryMapper by inject<CategoryMapper>()

    operator fun invoke(equipment: Equipment) = EquipmentDto(
        id = equipment.id.value,
        description = equipment.description,
        category = categoryMapper(category = equipment.category)
    )
}