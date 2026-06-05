package com.example.apptravelfood.ui.screen.checkinscreen

import com.example.apptravelfood.data.local.entity.CheckinEntity

data class CheckinUiState(
    val isLoading: Boolean = false,
    val hasCheckedToday: Boolean = false,
    val todayCheckin: CheckinEntity? = null,
    val totalPoint: Int = 0,

    val checkedDays: List<Int> = emptyList(), // ví dụ: [1,2,3]
    val message: String? = null,
    val error: String? = null
)