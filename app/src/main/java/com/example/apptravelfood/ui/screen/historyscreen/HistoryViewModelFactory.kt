package com.example.apptravelfood.ui.screen.historyscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.repository.CheckinRepository
import com.example.apptravelfood.data.repository.PointHistoryRepository

class HistoryViewModelFactory(
    private val checkinRepository: CheckinRepository,
    private val pointHistoryRepository: PointHistoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(
            checkinRepository = checkinRepository,
            pointHistoryRepository = pointHistoryRepository
        ) as T
    }
}