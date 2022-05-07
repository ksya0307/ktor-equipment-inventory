package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Ifo
import com.ksenialexeev.database.tables.Ifos
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.IfoMapper
import com.ksenialexeev.models.IfoDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface IfoManager {
    suspend fun getAll(): List<IfoDto>
    suspend fun delete(id: Int): HttpStatusCode
    suspend fun update(id: Int, name: String): IfoDto
    suspend fun create(dto: IfoDto): IfoDto
}

class IfoManagerImpl : IfoManager, KoinComponent {

    private val mapper by inject<IfoMapper>()
    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        Ifo.all().map(mapper::invoke)
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Ifo.findById(id)?.let { it.delete(); HttpStatusCode.OK } ?: throw  NotFoundException("Ifo", id)
    }

    override suspend fun update(id: Int, name: String) = newSuspendedTransaction(Dispatchers.IO) {
        val ifo = Ifo.find { Ifos.name eq name }
        if (ifo.empty()) {
            Ifo.findById(id)?.let {
                it.name = name
                mapper(it)
            } ?: throw NotFoundException("IFO", id)
        } else {
            throw NotFoundException("IFO already exists", name)
        }
    }

    override suspend fun create(dto: IfoDto) = newSuspendedTransaction(Dispatchers.IO) {
        val ifo = Ifo.find { Ifos.name eq dto.name }
        if (ifo.empty()) {
            Ifo.new {
                name = dto.name
            }.let { mapper(it) }
        } else {
            throw NotFoundException("Ifo", dto.name)
        }

    }


}