package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(val name: String)

@Serializable
data class CreateCategoryDto(val name:String)
