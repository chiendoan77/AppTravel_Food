package com.example.apptravelfood.ui.screen.profilescreen.term

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Điều khoản sử dụng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        AppPageSurface(modifier = Modifier.padding(padding), scrollable = true) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Điều khoản TravelFood",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppGreenStrong
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = """
1. Người dùng chịu trách nhiệm với thông tin quán ăn, món ăn và bình luận đã đăng.

2. Ứng dụng chỉ hỗ trợ lưu dữ liệu cục bộ trong quá trình học tập/đồ án.

3. Không đăng nội dung sai sự thật, xúc phạm, spam hoặc vi phạm pháp luật.

4. Điểm check-in chỉ dùng trong phạm vi ứng dụng.

5. Người dùng có thể chỉnh sửa thông tin cá nhân, đổi mật khẩu, đăng xuất hoặc xóa tài khoản.

6. Dữ liệu có thể bị mất nếu người dùng xóa ứng dụng hoặc xóa dữ liệu local.
                """.trimIndent(),
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp)
                )
            }
        }
    }
}