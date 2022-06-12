package com.ksenialexeev.database.managers

import com.ksenialexeev.database.tables.*
import com.ksenialexeev.exceptions.NotFoundException
import com.ksenialexeev.mappers.RepairEquipmentMapper
import com.ksenialexeev.models.CreateRepairEquipmentDto
import com.ksenialexeev.models.RepairEquipmentDto
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RepairEquipmentManager {
    suspend fun getAll(): List<RepairEquipmentDto>
    suspend fun create(dto: CreateRepairEquipmentDto): RepairEquipmentDto
    suspend fun update(repair_id: Int?, equipment_id: Int?, id:Int, problem:String?): RepairEquipmentDto
    suspend fun delete(id: Int): HttpStatusCode
    suspend fun getByUserId(user_id: Int): List<RepairEquipmentDto>
}

class RepairEquipmentManagerImpl : RepairEquipmentManager, KoinComponent {

    private val mapper by inject<RepairEquipmentMapper>()

    override suspend fun getAll() = newSuspendedTransaction(Dispatchers.IO) {
        RepairEquipment.all().map(mapper::invoke)
    }

    override suspend fun create(dto: CreateRepairEquipmentDto) = newSuspendedTransaction(Dispatchers.IO) {
        var repair = Repair.findById(dto.repair_id) ?: throw NotFoundException("Repair Not Found", dto.repair_id)
        var equipment = ClassroomsEquipment.findById(dto.equipment_id)?: throw NotFoundException("Equipment Not Found", dto.equipment_id)
        RepairEquipment.new {
            repair_id = repair
            equipment_id = equipment
            problem = dto.problem

        }.let { mapper(it) }
    }

    override suspend fun update(repair_id: Int?, equipment_id: Int?, id:Int, problem:String?) = newSuspendedTransaction(Dispatchers.IO) {
        val repairEquipment = RepairEquipment.findById(id) ?: throw NotFoundException("Repair with id $id not found", "")
        val equipment = equipment_id?.let { ClassroomsEquipment.findById(it) }
        val repair = repair_id?.let { Repair.findById(it) }
        if(repairEquipment!=null){
            RepairEquipment.findById(id)?.let {
                if (equipment != null) {
                    it.equipment_id=equipment
                }
                if (repair != null) {
                    it.repair_id=repair
                }
                if (problem != null && problem.isNotEmpty()) {
                    it.problem = problem
                }
                mapper(it)
            } ?: throw NotFoundException("Repair with id $id not found", "")
        }else{
             throw NotFoundException("Something went wrong", "")
        }
    }

    override suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        RepairEquipment.findById(id)?.let{it.delete();HttpStatusCode.OK} ?: throw NotFoundException("Repair with id $id not found", "")
    }

    override suspend fun getByUserId(user_id: Int) = newSuspendedTransaction(Dispatchers.IO) {
        RepairEquipments
            .innerJoin(ClassroomsEquipments)
            .innerJoin(Classrooms)
            .innerJoin(Repairs)
            .select {
                Classrooms.user eq user_id
            }.withDistinct()
            .let {
                if (it.empty()) {
                    throw NotFoundException("User not found:", user_id)
                } else {
                    RepairEquipment.wrapRows(it).map { repairs -> mapper(repairs) }
                }
            }


    }
}