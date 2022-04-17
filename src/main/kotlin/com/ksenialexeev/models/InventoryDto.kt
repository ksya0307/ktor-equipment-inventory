package com.ksenialexeev.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class InventoryDto(
    val id: Int,
    val inventory_number: ClassroomEquipmentDto,
    val get_date: LocalDate,
    val document: String,
    val ifo:String,
    val for_classroom: ClassroomDto,
    val responsible_person: UserDto,
    val given: Boolean
)