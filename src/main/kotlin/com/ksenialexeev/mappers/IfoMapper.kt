package com.ksenialexeev.mappers

import com.ksenialexeev.database.tables.Ifo
import com.ksenialexeev.models.IfoDto

class IfoMapper {
    operator fun invoke(ifo: Ifo) = IfoDto(
        name = ifo.name
    )
}