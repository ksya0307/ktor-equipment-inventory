package com.ksenialexeev.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable


@Serializable
data class RepairDto(val phone:String)

@Serializable
data class UpdateRepairDto(val phone:String, val datetime: LocalDate,val completed:Boolean)

@Serializable
data class RepairEquipmentDto(val repair: RepairDto, val equipment: ClassroomEquipmentDto?,val problem:String)

@Serializable
data class CreateRepairEquipmentDto(val repair_id: Int, val equipment_id: Int, val problem:String)