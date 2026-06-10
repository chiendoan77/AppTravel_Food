package com.example.apptravelfood.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.apptravelfood.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("UPDATE users SET fullName = :name WHERE userId = :userId")
    suspend fun updateName(userId: Long, name: String)

    @Query("UPDATE users SET email = :email WHERE userId = :userId")
    suspend fun updateEmail(userId: Long, email: String)

    @Query("UPDATE users SET phone = :phone WHERE userId = :userId")
    suspend fun updatePhone(userId: Long, phone: String)

    @Query("UPDATE users SET passwordHash = :password WHERE userId = :userId")
    suspend fun updatePassword(userId: Long, password: String)

    @Query("UPDATE users SET totalPoint = totalPoint + :point WHERE userId = :userId")
    suspend fun addPoint(userId: Long, point: Int)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("UPDATE users SET biometricEnabled = :enabled WHERE userId = :userId")
    suspend fun updateBiometricEnabled(
        userId: Long,
        enabled: Boolean
    )
    @Query("UPDATE users SET avatarUrl = :avatarUrl WHERE userId = :userId")
    suspend fun updateAvatar(
        userId: Long,
        avatarUrl: String
    )
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserReplace(user: UserEntity): Long
}