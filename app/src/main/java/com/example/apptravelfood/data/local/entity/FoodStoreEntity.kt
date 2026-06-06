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
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["createdByUserId"],
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

    val placeId: String,

    val createdByUserId: Long,

    val name: String,

    val address: String?,

    val imageUrl: String?,

    val description: String?,

    val createdAt: Long = System.currentTimeMillis()
)