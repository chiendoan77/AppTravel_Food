package com.example.apptravelfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,

    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String? = null,

    val role: String = "USER",
    val biometricEnabled: Boolean = false,

    val avatarUrl: String? = null,
    val totalPoint: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)