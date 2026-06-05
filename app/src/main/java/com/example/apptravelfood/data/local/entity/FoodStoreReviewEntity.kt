package com.example.apptravelfood.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_store_reviews",
    foreignKeys = [
        ForeignKey(
            entity = FoodStoreEntity::class,
            parentColumns = ["foodStoreId"],
            childColumns = ["foodStoreId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("foodStoreId"),
        Index("userId"),
        Index(
            value = ["foodStoreId", "userId"],
            unique = true
        )
    ]
)
data class FoodStoreReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val reviewId: Long = 0,

    val foodStoreId: Long,
    val userId: Long,

    val rating: Float,
    val comment: String,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)