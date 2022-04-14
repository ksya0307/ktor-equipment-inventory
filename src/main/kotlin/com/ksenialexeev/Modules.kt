package com.ksenialexeev

import com.ksenialexeev.database.managers.CategoryManager
import com.ksenialexeev.database.managers.CategoryManagerImpl
import com.ksenialexeev.database.managers.ClassroomManager
import com.ksenialexeev.database.managers.ClassroomManagerImpl
import com.ksenialexeev.mappers.CategoryMapper
import com.ksenialexeev.mappers.ClassroomMapper
import com.ksenialexeev.mappers.UserMapper
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
}

val managerModule = module {
    singleOf(::CategoryManagerImpl){
        bind<CategoryManager>()
    }
    singleOf(::ClassroomManagerImpl){
        bind<ClassroomManager>()
    }
}
