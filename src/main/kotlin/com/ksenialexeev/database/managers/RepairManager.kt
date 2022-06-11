package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Repair
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.RepairMapper
import com.ksenialexeev.models.RepairDto
import com.ksenialexeev.models.UpdateRepairDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RepairManager{
    suspend fun getAll():List<RepairDto>
    suspend fun create(dto: RepairDto):UpdateRepairDto
    suspend fun update(id:Int, phone:String, datetime:LocalDate):RepairDto
    suspend fun delete(id:Int):HttpStatusCode
    suspend fun getById(id:Int):RepairDto
}
class RepairManagerImpl:RepairManager, KoinComponent {

    private val mapper by inject<RepairMapper>()

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        Repair.all().map(mapper::invoke)
    }

    override suspend fun create(dto: RepairDto) = newSuspendedTransaction(Dispatchers.IO) {
        Repair.new {
            phone = dto.phone
            datetime = dto.datetime!!
        }.let {
            UpdateRepairDto(
                id = it.id.value,
                phone = it.phone,
                datetime = it.datetime,
                completed = it.completed
            )
        }
    }


    override suspend fun update(id:Int, phone:String, datetime:LocalDate) = newSuspendedTransaction(Dispatchers.IO) {
       Repair.findById(id)?.let {
           it.phone = phone
           it.datetime = datetime
           mapper(it)
       }?: throw NotFoundException("Repair not found:", id)
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
       Repair.findById(id)?.let { it.delete(); HttpStatusCode.OK } ?:  throw  NotFoundException("Repair not found", id)
    }

    override suspend fun getById(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Repair.findById(id)?.let { mapper(it) } ?: throw NotFoundException("Repair not found:", id)
    }

}