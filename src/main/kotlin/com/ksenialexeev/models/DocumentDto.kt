package com.ksenialexeev.models

import kotlinx.serialization.Serializable

@Serializable
data class DocumentDto(val name:String)

@Serializable
data class ChangeDocumentDto(val name: String)