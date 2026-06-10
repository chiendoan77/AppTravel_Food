package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.PointHistoryDao
import com.example.apptravelfood.data.local.entity.PointHistoryEntity

class   PointHistoryRepository(
    private val pointHistoryDao: PointHistoryDao
) {
    suspend fun addHistory(history: PointHistoryEntity): Long {
        return pointHistoryDao.insertPointHistory(history)
    }

    suspend fun getHistoryByUser(userId: Long): List<PointHistoryEntity> {
        return pointHistoryDao.getPointHistoryByUserId(userId)
    }
    suspend fun getPointHistoryById(pointHistoryId: Long): PointHistoryEntity? {
        return pointHistoryDao.getPointHistoryById(pointHistoryId)
    }

    suspend fun insertPointHistoryReplace(history: PointHistoryEntity): Long {
        return pointHistoryDao.insertPointHistoryReplace(history)
    }
}