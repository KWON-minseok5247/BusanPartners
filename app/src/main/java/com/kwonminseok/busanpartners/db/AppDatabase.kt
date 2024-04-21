import android.content.Context
import androidx.room.*


@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val FILENAME = "user_database"
        @Volatile
        var INSTANCE: AppDatabase? = null


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


        private fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                FILENAME
            ).fallbackToDestructiveMigration()
                .build()
        }

        fun init(context: Context): AppDatabase = INSTANCE ?:
        synchronized(this) {
            INSTANCE ?: create(context).also {
                INSTANCE = it
            }
        }
        fun getInstance(): AppDatabase = INSTANCE!!

    }
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