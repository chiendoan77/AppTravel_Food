package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.ui.components.AddTextField
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppAccentOutlinedButton
import com.example.apptravelfood.ui.components.AppConfirmDialog
import com.example.apptravelfood.ui.components.AppGlassCard
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
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
        if (editFoodItem != null) {
            viewModel.setEditFoodItem(editFoodItem)
        }
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
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    AddFoodItemScreen(
        uiState = uiState,
        store = store,
        onBack = onBack,
        onNameChange = viewModel::updateName,
        onDescriptionChange = viewModel::updateDescription,
        onPriceChange = viewModel::updatePrice,
        onImageUrlChange = viewModel::updateImageUrl,
        onSaveClick = {
            viewModel.saveFoodItem(context, store.foodStoreId)
        },
        onDeleteClick = {
            showDeleteConfirm = true
        },
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            onLocalImageSelected(uri)
        }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Chỉnh sửa món ăn" else "Thêm món mới",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, tint = AppGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->

        AppPageSurface(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Store Info Summary
                AppGlassCard {
                    Text(
                        text = "Quán ăn:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AppGreenStrong
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AppGlassCard {
                    // Image Picker
                    val previewImage =
                        uiState.localImageUri ?: uiState.imageUrl.takeIf { it.isNotBlank() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (previewImage != null) {
                            AsyncImage(
                                model = previewImage,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    "Thay đổi ảnh",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.AddPhotoAlternate,
                                    null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Thêm hình ảnh món ăn",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AddTextField(
                        value = uiState.name,
                        onValueChange = onNameChange,
                        label = "Tên món ăn",
                        placeholder = "Ví dụ: Bún chả đặc biệt",
                        leadingIcon = Icons.Default.RestaurantMenu
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AddTextField(
                        value = uiState.price,
                        onValueChange = onPriceChange,
                        label = "Giá bán (VNĐ)",
                        placeholder = "Ví dụ: 35000",
                        leadingIcon = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AddTextField(
                        value = uiState.description,
                        onValueChange = onDescriptionChange,
                        label = "Mô tả món ăn",
                        placeholder = "Nguyên liệu, hương vị...",
                        minLines = 3
                    )
                }

                uiState.error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it,
                        color = AppRed,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppAccentButton(
                    text = if (uiState.isSaving) "Đang lưu..." else if (uiState.isEditMode) "Cập nhật món ăn" else "Thêm món ngay",
                    onClick = onSaveClick,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                if (uiState.isEditMode) {
                    Spacer(modifier = Modifier.height(12.dp))
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
    }
}
