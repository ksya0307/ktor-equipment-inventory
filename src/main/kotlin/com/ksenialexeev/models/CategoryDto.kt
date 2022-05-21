package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(val id:Int ,val name: String)

@Serializable
data class CreateOrUpdateCategoryDto(val name:String)
