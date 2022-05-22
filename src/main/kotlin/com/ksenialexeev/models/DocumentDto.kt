package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class DocumentDto(val id:Int, val name:String)

@Serializable
data class CreateOrChangeDocumentDto(val name: String)

