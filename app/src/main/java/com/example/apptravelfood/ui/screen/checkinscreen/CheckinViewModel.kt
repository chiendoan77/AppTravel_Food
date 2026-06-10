package com.example.apptravelfood.ui.screen.checkinscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.core.untils.PasswordUtils
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.local.entity.CheckinEntity
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
    private val userRepository: UserRepository,
    private val firebaseRepository: FirebaseRepository
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

                val existedFirebase = firebaseRepository.getTodayCheckin(
                    userId = userId,
                    startOfDay = startOfDay,
                    endOfDay = endOfDay
                )

                if (existedFirebase != null) {
                    checkinRepository.insertCheckinReplace(existedFirebase)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasCheckedToday = true,
                        todayCheckin = existedFirebase,
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

                val checkinWithoutId = CheckinEntity(
                    userId = userId,
                    imageUrl = imageUrl,
                    pointEarned = pointEarned,
                    faceVerified = faceVerified
                )

                val newCheckinId = checkinRepository.insertCheckin(
                    checkinWithoutId
                )

                val checkinWithId = checkinWithoutId.copy(
                    checkinId = newCheckinId
                )

                try {
                    firebaseRepository.backupCheckin(checkinWithId)
                } catch (_: Exception) {
                }

                if (pointEarned > 0) {
                    userRepository.addPoint(
                        userId = userId,
                        point = pointEarned
                    )

                    val pointHistoryWithoutId = PointHistoryEntity(
                        userId = userId,
                        point = pointEarned,
                        type = "CHECKIN",
                        description = "Điểm danh ngày $nextDay +$pointEarned điểm"
                    )

                    val newPointHistoryId = pointHistoryRepository.addHistory(
                        pointHistoryWithoutId
                    )

                    val pointHistoryWithId = pointHistoryWithoutId.copy(
                        pointHistoryId = newPointHistoryId
                    )

                    try {
                        firebaseRepository.backupPointHistory(
                            pointHistoryWithId
                        )
                    } catch (_: Exception) {
                    }

                    val updatedUser = userRepository.getUser(userId)

                    if (updatedUser != null) {
                        try {
                            firebaseRepository.backupUser(updatedUser)
                        } catch (_: Exception) {
                        }
                    }
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
    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = message
        )
    }

    fun checkinWithPassword(
        userId: Long,
        password: String
    ) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(userId)

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Không tìm thấy tài khoản"
                    )
                    return@launch
                }

                if (user.authProvider == "GOOGLE") {
                    _uiState.value = _uiState.value.copy(
                        error = "Tài khoản Google không có mật khẩu app. Vui lòng dùng sinh trắc học."
                    )
                    return@launch
                }

                if (password.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        error = "Vui lòng nhập mật khẩu"
                    )
                    return@launch
                }

                val inputHash = PasswordUtils.hash(password)

                if (user.passwordHash.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        error = "Tài khoản chưa có mật khẩu hợp lệ. Hãy đặt lại mật khẩu."
                    )
                    return@launch
                }

                if (user.passwordHash != inputHash) {
                    _uiState.value = _uiState.value.copy(
                        error = "Mật khẩu không đúng"
                    )
                    return@launch
                }

                checkinToday(
                    userId = userId,
                    imageUrl = null,
                    faceVerified = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Không xác thực được mật khẩu"
                )
            }
        }
    }
}