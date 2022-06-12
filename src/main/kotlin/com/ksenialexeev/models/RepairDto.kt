package com.ksenialexeev.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class RepairDto(
    val id:Int,
    val phone: String,
    val datetime: LocalDate
)

@Serializable
data class CreateRepairDto(
    val phone: String,
    val datetime: LocalDate? = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
)

@Serializable
data class UpdateRepairDto(
    val id: Int,
    val phone: String? = null,
    val datetime: LocalDate? = null,
    val completed: Boolean? = null
)

@Serializable
data class RepairEquipmentDto(val id:Int,val repair: RepairDto, val equipment: ClassroomEquipmentDto?, val problem: String)


@Serializable
data class CreateRepairEquipmentDto(val repair_id: Int, val equipment_id: Int, val problem: String)

@Serializable
data class UpdateRepairEquipmentDto(
    val repair_id: Int? = null,
    val equipment_id: Int? = null,
    val problem: String? = null
)