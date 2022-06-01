package com.ksenialexeev.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class InventoryDto(
    val id: Int,
    val inventory_number: ClassroomEquipmentDto,
    val get_date: LocalDate,
    val document: DocumentDto,
    val ifo:IfoDto,
    val for_classroom: ClassroomDto,
    val given: Boolean,
    val by_request: Boolean
)

@Serializable
data class CreateInventoryDto(
    val inventory_number:Int,
    val get_date: LocalDate,
    val document: String,
    val ifo:String,
    val for_classroom: String,
    val given: Boolean,
    val by_request: Boolean
)