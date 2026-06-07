package com.example.apptravelfood.data.local.dao

import androidx.room.*
import com.example.apptravelfood.data.local.entity.CheckinEntity

@Dao
interface CheckinDao {

    @Insert
    suspend fun insertCheckin(checkin: CheckinEntity): Long

    @Query("SELECT * FROM checkins WHERE userId = :userId ORDER BY checkinTime DESC")
    suspend fun getCheckinsByUserId(userId: Long): List<CheckinEntity>

    @Query("""
        SELECT * FROM checkins 
        WHERE userId = :userId 
        AND checkinTime BETWEEN :startOfDay AND :endOfDay
        LIMIT 1
    """)
    suspend fun getTodayCheckin(
        userId: Long,
        startOfDay: Long,
        endOfDay: Long
    ): CheckinEntity?

    @Query("SELECT * FROM checkins WHERE checkinId = :checkinId LIMIT 1")
    suspend fun getCheckinById(checkinId: Long): CheckinEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckinReplace(checkin: CheckinEntity): Long
}