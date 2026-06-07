package com.example.apptravelfood.ui.screen.profilescreen.term

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Điều khoản TravelFood",
                style = MaterialTheme.typography.headlineSmall
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
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}