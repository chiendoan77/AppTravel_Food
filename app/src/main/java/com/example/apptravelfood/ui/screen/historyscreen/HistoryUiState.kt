package com.example.apptravelfood.ui.screen.historyscreen

import com.example.apptravelfood.data.local.entity.CheckinEntity
import com.example.apptravelfood.data.local.entity.PointHistoryEntity

data class HistoryUiState(
    val isLoading: Boolean = false,
    val checkins: List<CheckinEntity> = emptyList(),
    val pointHistories: List<PointHistoryEntity> = emptyList(),
    val error: String? = null
)