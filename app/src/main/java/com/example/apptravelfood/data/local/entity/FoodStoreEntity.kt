package com.example.apptravelfood.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_stores",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["placeId"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("placeId"),
        Index("createdByUserId")
    ]
)
data class FoodStoreEntity(
    @PrimaryKey(autoGenerate = true)
    val foodStoreId: Long = 0,

    val placeId: String = "",

    val createdByUserId: Long = 0,

    val name: String = "",

    val address: String? = null,

    val imageUrl: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    val description: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)