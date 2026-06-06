package com.example.apptravelfood.ui.screen.checkinscreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.repository.CheckinRepository
import com.example.apptravelfood.data.repository.PointHistoryRepository
import com.example.apptravelfood.data.repository.UserRepository

class CheckinViewModelFactory(
    private val checkinRepository: CheckinRepository,
    private val pointHistoryRepository: PointHistoryRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CheckinViewModel(
            checkinRepository = checkinRepository,
            pointHistoryRepository = pointHistoryRepository,
            userRepository = userRepository
        ) as T
    }
}