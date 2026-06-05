package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.UserDao
import com.example.apptravelfood.data.local.entity.UserEntity

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun createUser(user: UserEntity): Long {
        return userDao.insertUser(user)
    }

    suspend fun getUser(userId: Long): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun updateName(userId: Long, name: String) {
        userDao.updateName(userId, name)
    }

    suspend fun updateEmail(userId: Long, email: String) {
        userDao.updateEmail(userId, email)
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
}