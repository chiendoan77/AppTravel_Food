package com.example.apptravelfood.ui.screen.checkinscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.local.entity.PointHistoryEntity
import com.example.apptravelfood.data.repository.CheckinRepository
import com.example.apptravelfood.data.repository.PointHistoryRepository
import com.example.apptravelfood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class CheckinViewModel(
    private val checkinRepository: CheckinRepository,
    private val pointHistoryRepository: PointHistoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckinUiState())
    val uiState: StateFlow<CheckinUiState> = _uiState.asStateFlow()

    fun loadCheckinData(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val startOfDay = getStartOfDay()
                val endOfDay = getEndOfDay()

                val todayCheckin = checkinRepository.getTodayCheckin(
                    userId = userId,
                    startOfDay = startOfDay,
                    endOfDay = endOfDay
                )

                val history = checkinRepository.getCheckinsByUser(userId)
                val user = userRepository.getUser(userId)

                val checkedDays = List(
                    history
                        .take(7).size) { index -> index + 1 }

                _uiState.value = CheckinUiState(
                    isLoading = false,
                    hasCheckedToday = todayCheckin != null,
                    todayCheckin = todayCheckin,
                    totalPoint = user?.totalPoint ?: 0,
                    checkedDays = checkedDays
                )

            } catch (e: Exception) {
                _uiState.value = CheckinUiState(
                    isLoading = false,
                    error = e.message ?: "Không tải được dữ liệu check-in"
                )
            }
        }
    }

    fun checkinToday(
        userId: Long,
        imageUrl: String?,
        faceVerified: Boolean
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val startOfDay = getStartOfDay()
                val endOfDay = getEndOfDay()

                val existed = checkinRepository.getTodayCheckin(
                    userId = userId,
                    startOfDay = startOfDay,
                    endOfDay = endOfDay
                )

                if (existed != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasCheckedToday = true,
                        todayCheckin = existed,
                        message = "Hôm nay bạn đã điểm danh rồi"
                    )
                    return@launch
                }

                val history = checkinRepository.getCheckinsByUser(userId)

                val nextDay = (history.size % 7) + 1

                val pointEarned = if (faceVerified) {
                    getPointByDay(nextDay)
                } else {
                    0
                }

                checkinRepository.checkin(
                    userId = userId,
                    imageUrl = imageUrl,
                    pointEarned = pointEarned,
                    faceVerified = faceVerified
                )

                if (pointEarned > 0) {
                    userRepository.addPoint(
                        userId = userId,
                        point = pointEarned
                    )

                    pointHistoryRepository.addHistory(
                        PointHistoryEntity(
                            userId = userId,
                            point = pointEarned,
                            type = "CHECKIN",
                            description = "Điểm danh ngày $nextDay +$pointEarned điểm"
                        )
                    )
                }

                loadCheckinData(userId)

                _uiState.value = _uiState.value.copy(
                    message = "Điểm danh thành công +$pointEarned điểm"
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Điểm danh thất bại"
                )
            }
        }
    }

    private fun getPointByDay(day: Int): Int {
        return when (day) {
            1 -> 5
            2 -> 5
            3 -> 10
            4 -> 10
            5 -> 15
            6 -> 15
            7 -> 30
            else -> 0
        }
    }

    private fun getStartOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getEndOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}