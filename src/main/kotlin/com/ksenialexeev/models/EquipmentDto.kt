package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDto(val id: Int, val description: String, val category: CategoryDto)