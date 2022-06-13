package com.ksenialexeev.models


import com.ksenialexeev.dateTimeInUtc
import kotlinx.serialization.Serializable

import kotlinx.datetime.LocalDateTime


@Serializable
data class RepairDto(
    val id:Int,
    val phone: String,
    val datetime: LocalDateTime
)



@Serializable
data class CreateRepairDto(
    val phone: String,
    val datetime: String? = dateTimeInUtc.toString()
)

@Serializable
data class UpdateRepairDto(
    val id: Int,
    val phone: String? = null,
    val datetime: LocalDateTime? = null,
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