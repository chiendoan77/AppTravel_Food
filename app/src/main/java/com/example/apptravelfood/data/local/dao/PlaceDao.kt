package com.example.apptravelfood.data.local.dao

import androidx.room.*
import com.example.apptravelfood.data.local.entity.PlaceEntity

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity)

    @Query("SELECT * FROM places WHERE placeId = :placeId LIMIT 1")
    suspend fun getPlaceById(placeId: String): PlaceEntity?
}