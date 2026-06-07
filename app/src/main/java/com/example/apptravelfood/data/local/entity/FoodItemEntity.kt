package com.example.apptravelfood.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_items",
    foreignKeys = [
        ForeignKey(
            entity = FoodStoreEntity::class,
            parentColumns = ["foodStoreId"],
            childColumns = ["foodStoreId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("foodStoreId")
    ]
)
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true)
    val foodItemId: Long = 0,

    val foodStoreId: Long = 0,

    val name: String = "",
    val description: String? = null,
    val price: Double? = null,
    val imageUrl: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)