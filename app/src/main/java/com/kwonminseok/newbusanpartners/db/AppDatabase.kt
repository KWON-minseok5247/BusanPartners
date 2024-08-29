package com.kwonminseok.newbusanpartners.db


import androidx.room.*
import com.kwonminseok.newbusanpartners.db.dao.UserDao
import com.kwonminseok.newbusanpartners.db.entity.UserEntity


@Database(entities = [UserEntity::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

//    companion object {
//        const val FILENAME = "user_database"
//        @Volatile
//        var INSTANCE: AppDatabase? = null
//
//
//        fun getDatabase(
//            context: Context
//        ): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    FILENAME
//                )
//                    .fallbackToDestructiveMigration()
//                    .build()
//
//                INSTANCE = instance
//                instance
//            }
//        }
//
//    }

//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//        fun getDatabase(
//            context: Context,
////            scope: CoroutineScope
//        ): AppDatabase {
//            // if the INSTANCE is not null, then return it,
//            // if it is, then create the database
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "user_database"
//                )   .fallbackToDestructiveMigration()
//                    .build()
//                INSTANCE = instance
//                // return instance
//                instance
//            }
//        }
//    }

}

//
//@Database(
//    entities = [
//        UserEntity::class,
//    QuestionEntity::class
//    ], version = 1
//)
//@TypeConverters(Converters::class)
//abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun getUserDao(): UserDao
//    abstract fun getQuestionDao(): QuestionDao
//
//    companion object {
//        const val FILENAME = "dailyq.db"
//        @Volatile var INSTANCE: AppDatabase? = null
//
//        private fun create(context: Context): AppDatabase {
//            return Room.databaseBuilder(
//                context.applicationContext,
//                AppDatabase::class.java,
//                FILENAME
//            ).fallbackToDestructiveMigration()
//                .build()
//        }
//
//        fun init(context: Context): AppDatabase = INSTANCE ?:
//        synchronized(this) {
//            INSTANCE ?: create(context).also {
//                INSTANCE = it
//            }
//        }
//        fun getInstance(): AppDatabase = INSTANCE!!
//
//    }
//}


//
//
//        private fun create(context: Context): AppDatabase {
//            return Room.databaseBuilder(
//                context.applicationContext,
//                AppDatabase::class.java,
//                FILENAME
//            ).fallbackToDestructiveMigration()
//                .build()
//        }
//
//        fun init(context: Context): AppDatabase = INSTANCE ?:
//        synchronized(this) {
//            INSTANCE ?: create(context).also {
//                INSTANCE = it
//            }
//        }
//        fun getInstance(): AppDatabase = INSTANCE!!
