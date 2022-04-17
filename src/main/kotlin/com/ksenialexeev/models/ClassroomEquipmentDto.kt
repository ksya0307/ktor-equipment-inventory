package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class ClassroomEquipmentDto(
    val id: Int,
    val inventory_number: Long,
    val classroom: ClassroomDto,
    val equipment: EquipmentDto,
    val number_in_classroom:String,
    val equipment_type: String
)
