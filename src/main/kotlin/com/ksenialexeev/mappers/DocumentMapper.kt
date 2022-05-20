package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Document
import com.ksenialexeev.models.DocumentDto

class DocumentMapper {
    operator fun invoke(document: Document) = DocumentDto(
        id = document.id.value,
        name = document.name
    )
}