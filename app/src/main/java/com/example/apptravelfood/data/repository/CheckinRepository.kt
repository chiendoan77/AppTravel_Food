package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.CheckinDao
import com.example.apptravelfood.data.local.entity.CheckinEntity

class CheckinRepository(
    private val checkinDao: CheckinDao
) {
    suspend fun checkin(
        userId: Long,
        imageUrl: String?,
        pointEarned: Int,
        faceVerified: Boolean
    ): Long {
        return checkinDao.insertCheckin(
            CheckinEntity(
                userId = userId,
                imageUrl = imageUrl,
                pointEarned = pointEarned,
                faceVerified = faceVerified
            )
        )
    }

    suspend fun getCheckinsByUser(userId: Long): List<CheckinEntity> {
        return checkinDao.getCheckinsByUserId(userId)
    }

    suspend fun getTodayCheckin(
        userId: Long,
        startOfDay: Long,
        endOfDay: Long
    ): CheckinEntity? {
        return checkinDao.getTodayCheckin(
            userId = userId,
            startOfDay = startOfDay,
            endOfDay = endOfDay
        )
    }
}