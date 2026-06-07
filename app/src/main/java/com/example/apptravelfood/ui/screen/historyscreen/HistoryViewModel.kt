package com.example.apptravelfood.ui.screen.historyscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.repository.CheckinRepository
import com.example.apptravelfood.data.repository.PointHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val checkinRepository: CheckinRepository,
    private val pointHistoryRepository: PointHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadHistory(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val checkins = checkinRepository.getCheckinsByUser(userId)
                val points = pointHistoryRepository.getHistoryByUser(userId)

                _uiState.value = HistoryUiState(
                    isLoading = false,
                    checkins = checkins,
                    pointHistories = points
                )

            } catch (e: Exception) {
                _uiState.value = HistoryUiState(
                    isLoading = false,
                    error = e.message ?: "Không tải được lịch sử"
                )
            }
        }
    }
}