package com.ksenialexeev.database.managers

import com.ksenialexeev.models.EquipmentDto

interface EquipmentManager {
    suspend fun getById(id:Int):List<EquipmentDto>
}