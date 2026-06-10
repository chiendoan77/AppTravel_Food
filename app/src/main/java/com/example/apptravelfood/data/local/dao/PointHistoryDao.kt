package com.example.apptravelfood.data.local.dao

import androidx.room.*
import com.example.apptravelfood.data.local.entity.PointHistoryEntity

@Dao
interface PointHistoryDao {

    @Insert
    suspend fun insertPointHistory(history: PointHistoryEntity): Long

    @Query("SELECT * FROM point_history WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getPointHistoryByUserId(userId: Long): List<PointHistoryEntity>

    @Query("SELECT * FROM point_history WHERE pointHistoryId = :pointHistoryId LIMIT 1")
    suspend fun getPointHistoryById(pointHistoryId: Long): PointHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointHistoryReplace(history: PointHistoryEntity): Long

}