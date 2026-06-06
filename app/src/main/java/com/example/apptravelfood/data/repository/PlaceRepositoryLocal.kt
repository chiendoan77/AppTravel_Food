package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.PlaceDao
import com.example.apptravelfood.data.local.entity.PlaceEntity

class PlaceRepositoryLocal(
    private val placeDao: PlaceDao
) {
    suspend fun savePlace(place: PlaceEntity) {
        placeDao.insertPlace(place)
    }

    suspend fun getPlace(placeId: String): PlaceEntity? {
        return placeDao.getPlaceById(placeId)
    }
}