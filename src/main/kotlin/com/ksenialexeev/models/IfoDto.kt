package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class IfoDto(val name:String)

@Serializable
data class UpdateIfoDto(val id:Int, val name: String)



