package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.UserDao
import com.example.apptravelfood.data.local.entity.UserEntity

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun insertUserReplace(user: UserEntity): Long {
        return userDao.insertUserReplace(user)
    }
    suspend fun createUser(user: UserEntity): Long {
        return userDao.insertUser(user)
    }

    suspend fun getUser(userId: Long): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun updateName(userId: Long, name: String) {
        userDao.updateName(userId, name)
    }

    suspend fun updatePhone(userId: Long, phone: String) {
        userDao.updatePhone(userId, phone)
    }

    suspend fun updatePassword(userId: Long, password: String) {
        userDao.updatePassword(userId, password)
    }

    suspend fun addPoint(userId: Long, point: Int) {
        userDao.addPoint(userId, point)
    }
    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
    suspend fun updateBiometricEnabled(
        userId: Long,
        enabled: Boolean
    ) {
        userDao.updateBiometricEnabled(
            userId = userId,
            enabled = enabled
        )
    }
    suspend fun updateAvatar(
        userId: Long,
        avatarUrl: String
    ) {
        userDao.updateAvatar(userId, avatarUrl)
    }
}