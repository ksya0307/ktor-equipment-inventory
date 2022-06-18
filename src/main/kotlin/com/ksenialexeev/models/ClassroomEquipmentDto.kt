package com.ksenialexeev.models

import com.ksenialexeev.database.tables.EquipmentBelonging
import kotlinx.serialization.Serializable

@Serializable
data class ClassroomEquipmentDto(
    val id: Int,
    val inventory_number: Long,
    val classroom: ClassroomDto,
    val equipment: EquipmentDto,
    val number_in_classroom: String,
    val equipment_type: EquipmentBelonging
)

@Serializable
data class CreateClassroomEquipmentDto(
    val inventory_number: Long,
    val classroom: String? = null,
    val equipment: Int,
    val number_in_classroom: String? = null,
    val equipment_type: EquipmentBelonging
)

@Serializable
data class UpdateClassroomEquipmentDto(
    val id: Int,
    val inventory_number: Long ? =null,
    val classroom: String? = null,
    val equipment: String? = null,
    val number_in_classroom: String? = null,
    val equipment_type: EquipmentBelonging? = null
)

@Serializable
data class EquipmentSpecsDto(
    val inventory_number: Long,
    val classroom: ClassroomDto,
    val equipment: EquipmentDto,
    val equipment_type: EquipmentBelonging,
    val number_in_classroom: String
)