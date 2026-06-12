package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.CheckinDao
import com.example.apptravelfood.data.local.entity.CheckinEntity

class CheckinRepository(
    private val checkinDao: CheckinDao
) {

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

    suspend fun getCheckinById(checkinId: Long): CheckinEntity? {
        return checkinDao.getCheckinById(checkinId)
    }

    suspend fun insertCheckinReplace(checkin: CheckinEntity): Long {
        return checkinDao.insertCheckinReplace(checkin)
    }

    suspend fun insertCheckin(checkin: CheckinEntity): Long {
        return checkinDao.insertCheckin(checkin)
    }

}