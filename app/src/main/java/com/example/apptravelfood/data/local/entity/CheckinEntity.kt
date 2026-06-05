package com.example.apptravelfood.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "checkins",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
    ]
)
data class CheckinEntity(
    @PrimaryKey(autoGenerate = true)
    val checkinId: Long = 0,

    val userId: Long,

    val imageUrl: String?,
    val pointEarned: Int,
    val faceVerified: Boolean,

    val checkinTime: Long = System.currentTimeMillis()
)