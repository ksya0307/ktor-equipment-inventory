package com.ksenialexeev

import com.ksenialexeev.database.managers.*
import com.ksenialexeev.mappers.CategoryMapper
import com.ksenialexeev.mappers.ClassroomMapper
import com.ksenialexeev.mappers.UserMapper
import com.ksenialexeev.mappers.UserLoginMapper
import com.ksenialexeev.mappers.CreateUserMapper
import com.ksenialexeev.mappers.InventoryMapper
import com.ksenialexeev.mappers.CommentMapper
import com.ksenialexeev.mappers.ClassroomEquipmentMapper
import com.ksenialexeev.mappers.EquipmentMapper
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val jsonModule = module {
    single {
        Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
        }
    }
}

val mappersModule = module {
    singleOf(::CategoryMapper)
    singleOf(::ClassroomMapper)
    singleOf(::UserMapper)
    singleOf(::EquipmentMapper)
    singleOf(::CommentMapper)
    singleOf(::InventoryMapper)
    singleOf(::ClassroomEquipmentMapper)
    singleOf(::UserLoginMapper)
    singleOf(::CreateUserMapper)
}

val managerModule = module {
    singleOf(::CategoryManagerImpl){
        bind<CategoryManager>()
    }
    singleOf(::ClassroomManagerImpl){
        bind<ClassroomManager>()
    }
    singleOf(::CommentManagerImpl){
        bind<CommentManager>()
    }
    singleOf(::ClassroomEquipmentManagerImpl){
        bind<ClassroomEquipmentManager>()
    }
    singleOf(::UserManagerImpl){
        bind<UserManager>()
    }
}
