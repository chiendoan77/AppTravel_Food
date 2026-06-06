package com.example.apptravelfood.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "point_history",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId")
    ]
)
data class PointHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val pointHistoryId: Long = 0,

    val userId: Long,

    val point: Int,

    val type: String,

    val description: String?,

    val createdAt: Long = System.currentTimeMillis()
)