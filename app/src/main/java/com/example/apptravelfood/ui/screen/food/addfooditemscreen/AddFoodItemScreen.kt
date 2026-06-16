package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppAccentOutlinedButton
import com.example.apptravelfood.ui.components.AppConfirmDialog
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppRed

@Composable
fun AddFoodItemRoute(
    viewModel: AddFoodItemViewModel,
    store: FoodStoreEntity,
    editFoodItem: FoodItemEntity? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(editFoodItem?.foodItemId) {
        if (editFoodItem != null) viewModel.setEditFoodItem(editFoodItem)
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            val message =
                if (uiState.isEditMode) "Cập nhật món ăn thành công" else "Thêm món ăn thành công"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    AddFoodItemScreen(
        uiState = uiState,
        store = store,
        onBack = onBack,
        onNameChange = viewModel::updateName,
        onDescriptionChange = viewModel::updateDescription,
        onPriceChange = viewModel::updatePrice,
        onImageUrlChange = viewModel::updateImageUrl,
        onSaveClick = { viewModel.saveFoodItem(context, store.foodStoreId) },
        onDeleteClick = { showDeleteConfirm = true },
        onLocalImageSelected = viewModel::updateLocalImage
    )

    AppConfirmDialog(
        show = showDeleteConfirm,
        title = "Xóa món ăn",
        message = "Bạn có chắc chắn muốn xóa món ăn này không?",
        confirmText = "Xóa ngay",
        isDestructive = true,
        onConfirm = {
            showDeleteConfirm = false
            viewModel.deleteFoodItem(
                foodStoreId = store.foodStoreId,
                onDeleted = {
                    Toast.makeText(context, "Đã xóa món ăn", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            )
        },
        onDismiss = { showDeleteConfirm = false }
    )
}

@Composable
fun AddFoodItemScreen(
    uiState: AddFoodItemUiState,
    store: FoodStoreEntity,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLocalImageSelected: (Uri?) -> Unit,
) {
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> onLocalImageSelected(uri) }

    val previewImage = uiState.localImageUri ?: uiState.imageUrl.takeIf { it.isNotBlank() }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Scrollable body ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Space for fixed top bar
            Spacer(modifier = Modifier.height(64.dp))

            // ── Hero image ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFFF0F0F0))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (previewImage != null) {
                    AsyncImage(
                        model = previewImage,
                        contentDescription = "Ảnh món ăn",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.45f)
                                    ),
                                    startY = 120f
                                )
                            )
                    )
                    // Change photo chip
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Thay ảnh",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(AppGreenLight.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = AppGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Chạm để thêm ảnh món ăn",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            "Nên chọn ảnh rõ nét, tỉ lệ 4:3",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Form fields ───────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // Store label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppGreenLight.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Default.Store,
                        contentDescription = null,
                        tint = AppGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "Quán ăn",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            store.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppGreenStrong
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section label
                SectionLabel("Thông tin món ăn")

                Spacer(modifier = Modifier.height(12.dp))

                // Name field
                FoodTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = "Tên món ăn",
                    placeholder = "Ví dụ: Bún chả đặc biệt",
                    leadingIcon = Icons.Default.RestaurantMenu
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Price field — highlighted with green tint
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppGreenLight.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    FoodTextField(
                        value = uiState.price,
                        onValueChange = onPriceChange,
                        label = "Giá bán (VNĐ)",
                        placeholder = "35000",
                        leadingIcon = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description field
                FoodTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    label = "Mô tả món ăn",
                    placeholder = "Nguyên liệu, hương vị đặc trưng...",
                    leadingIcon = Icons.Default.EditNote,
                    minLines = 3
                )

                // Error
                uiState.error?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        color = AppRed,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

                Spacer(modifier = Modifier.height(20.dp))

                // Save button
                AppAccentButton(
                    text = when {
                        uiState.isSaving -> "Đang lưu..."
                        uiState.isEditMode -> "Cập nhật món ăn"
                        else -> "Thêm món ngay"
                    },
                    onClick = onSaveClick,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                if (uiState.isEditMode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    AppAccentOutlinedButton(
                        text = "Xóa món ăn này",
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        color = AppRed
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // ── Fixed top bar ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .shadow(elevation = 2.dp)
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppGreenLight.copy(alpha = 0.18f))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Quay lại",
                    tint = AppGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (uiState.isEditMode) "Chỉnh sửa món ăn" else "Thêm món mới",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppGreenStrong
            )
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppGreen,
        letterSpacing = 0.5.sp
    )
}

// ── Unified text field ────────────────────────────────────────────────────────
@Composable
private fun FoodTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.LightGray) },
        leadingIcon = {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = AppGreen,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = minLines == 1,
        minLines = minLines,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppGreen,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f),
            focusedLabelColor = AppGreen,
            cursorColor = AppGreen
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}