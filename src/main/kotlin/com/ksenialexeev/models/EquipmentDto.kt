package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDto(val id: Int, val description: String, val category: CategoryDto)

@Serializable
data class CreateEquipmentDto(val description: String, val category: Int)

@Serializable
data class UpdateEquipmentDto(val id:Int,val description: String, val categoryId:Int)