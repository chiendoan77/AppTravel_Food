package com.example.apptravelfood.core.di

import com.example.apptravelfood.data.local.database.AppDatabase
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.CheckinRepository
import com.example.apptravelfood.data.repository.FoodItemRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import com.example.apptravelfood.data.repository.FoodStoreReviewRepository
import com.example.apptravelfood.data.repository.PlaceRepositoryLocal
import com.example.apptravelfood.data.repository.PointHistoryRepository
import com.example.apptravelfood.data.repository.SyncRepository
import com.example.apptravelfood.data.repository.UserRepository

object AppContainer {

    private lateinit var database: AppDatabase

    val firebaseRepository: FirebaseRepository by lazy {
        FirebaseRepository()
    }
    fun init(
        database: AppDatabase
    ) {
        this.database = database
    }

    val userRepository: UserRepository by lazy {
        UserRepository(
            database.userDao()
        )
    }

    val placeRepository: PlaceRepositoryLocal by lazy {
        PlaceRepositoryLocal(
            database.placeDao()
        )
    }

    val foodStoreRepository: FoodStoreRepository by lazy {
        FoodStoreRepository(
            database.foodStoreDao()
        )
    }

    val foodItemRepository: FoodItemRepository by lazy {
        FoodItemRepository(
            database.foodItemDao()
        )
    }

    val foodStoreReviewRepository: FoodStoreReviewRepository by lazy {
        FoodStoreReviewRepository(
            database.foodStoreReviewDao()
        )
    }

    val checkinRepository: CheckinRepository by lazy {
        CheckinRepository(
            database.checkinDao()
        )
    }

    val pointHistoryRepository: PointHistoryRepository by lazy {
        PointHistoryRepository(
            database.pointHistoryDao()
        )
    }

    val syncRepository: SyncRepository by lazy {
        SyncRepository(
            firebaseRepository = firebaseRepository,
            userRepository = userRepository,
            foodStoreRepository = foodStoreRepository,
            foodItemRepository = foodItemRepository,
            reviewRepository = foodStoreReviewRepository,
            checkinRepository = checkinRepository,
            pointHistoryRepository = pointHistoryRepository
        )
    }
}