package com.example.apptravelfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,

    val firebaseUid: String? = null,

    val fullName: String = "",
    val email: String = "",
    val phone: String? = null,

    val passwordHash: String = "",

    val authProvider: String = "EMAIL",

    val emailVerified: Boolean = false,

    val role: String = "USER",
    val biometricEnabled: Boolean = false,

    val avatarUrl: String? = null,
    val totalPoint: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)