package com.kwonminseok.newbusanpartners.db.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kwonminseok.newbusanpartners.db.entity.UserEntity


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

//    @Query("SELECT * FROM user")
//    fun getUser(): LiveData<UserEntity>

    @Query("SELECT * FROM user WHERE uid = :userId")
    fun getUser(userId: String): LiveData<UserEntity?>
}

//@Dao
//interface UserDao {
//
//    @Insert
//    suspend fun insert(vararg users: UserEntity)
//
//    @Update
//    suspend fun update(vararg users: UserEntity)
//
//    @Delete
//    suspend fun delete(vararg userEntity: UserEntity)
//
//    @Query("SELECT * FROM user WHERE id=:uid")
//    suspend fun get(uid: String): UserEntity?
//
//}