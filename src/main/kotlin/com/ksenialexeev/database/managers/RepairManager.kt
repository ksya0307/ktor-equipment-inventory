package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.Repair
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.RepairMapper
import com.ksenialexeev.models.CreateRepairDto
import com.ksenialexeev.models.RepairDto
import com.ksenialexeev.models.UpdateRepairDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.datetime.LocalDateTime


interface RepairManager{
    suspend fun getAll():List<RepairDto>
    suspend fun create(dto: CreateRepairDto):UpdateRepairDto
    suspend fun update(id:Int, phone:String?, datetime: LocalDateTime?):RepairDto
    suspend fun delete(id:Int):HttpStatusCode
    suspend fun getById(id:Int):RepairDto
}
class RepairManagerImpl:RepairManager, KoinComponent {

    private val mapper by inject<RepairMapper>()

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        Repair.all().map(mapper::invoke)
    }

    override suspend fun create(dto: CreateRepairDto) = newSuspendedTransaction(Dispatchers.IO) {
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


    override suspend fun update(id:Int, phone:String?, datetime: LocalDateTime?) = newSuspendedTransaction(Dispatchers.IO) {

           Repair.findById(id)?.let {
               if (phone != null && phone.isNotEmpty()) {
                   it.phone = phone
               }
               if (datetime != null) {
                   it.datetime = datetime
               }

               mapper(it)
           }
        ?: throw NotFoundException("Repair not found", "")
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
       Repair.findById(id)?.let { it.delete(); HttpStatusCode.OK } ?:  throw  NotFoundException("Repair not found", id)
    }

    override suspend fun getById(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        Repair.findById(id)?.let { mapper(it) } ?: throw NotFoundException("Repair not found:", id)
    }

}