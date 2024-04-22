package com.kwonminseok.busanpartners.repository

import androidx.lifecycle.LiveData
import com.kwonminseok.busanpartners.db.AppDatabase
import com.kwonminseok.busanpartners.db.dao.UserDao
import com.kwonminseok.busanpartners.db.entity.UserEntity


//class RoomUserRepository(mDatabase: AppDatabase) {
//
//    private val userDao = mDatabase.userDao()
//    companion object {
//        private var sInstance: RoomUserRepository? = null
//        fun getInstance(database: AppDatabase): RoomUserRepository {
//            return sInstance
//                ?: synchronized(this) {
//                    val instance = RoomUserRepository(database)
//                    sInstance = instance
//                    instance
//                }
//        }
//    }
//
//
//    suspend fun insertUser(user: UserEntity) {
//        userDao.insertUser(user)
//    }
//
//     suspend fun updateUser(user: UserEntity) {
//        userDao.updateUser(user)
//    }
//
//     suspend fun deleteUser(user: UserEntity) {
//        userDao.deleteUser(user)
//    }
//
//     fun getUser(userId: String): LiveData<UserEntity?> = userDao.getUser(userId)
//
//
//}


interface RoomUserRepository {

    //Room 전용 함수
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(user: UserEntity)
    fun getUser(userId: String): LiveData<UserEntity?>



}

//class RoomUserRepositoryImpl(
//    userDao: UserDao,
//) : RoomUserRepository {
//
//    private val userDao = appDatabase.userDao()
//    companion object {
//        private var sInstance: RoomUserRepository? = null
//        fun getInstance(database: AppDatabase): RoomUserRepository {
//            return sInstance
//                ?: synchronized(this) {
//                    val instance = RoomUserRepositoryImpl(database)
//                    sInstance = instance
//                    instance
//                }
//        }
//    }
//
//    override suspend fun insertUser(user: UserEntity) {
//        userDao.insertUser(user)
//    }
//
//    override suspend fun updateUser(user: UserEntity) {
//        userDao.updateUser(user)
//    }
//
//    override suspend fun deleteUser(user: UserEntity) {
//        userDao.deleteUser(user)
//    }
//
//    override fun getUser(userId: String): LiveData<UserEntity?> = userDao.getUser(userId)
//
//
//}


class RoomUserRepositoryImpl(
    private val userDao: UserDao,
    ) : RoomUserRepository {
    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    override suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
    }

    override fun getUser(userId: String): LiveData<UserEntity?> = userDao.getUser(userId)


}
