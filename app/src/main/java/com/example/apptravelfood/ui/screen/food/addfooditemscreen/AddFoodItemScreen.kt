package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity

@Composable
fun AddFoodItemRoute(
    viewModel: AddFoodItemViewModel,
    store: FoodStoreEntity,
    editFoodItem: FoodItemEntity? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(editFoodItem?.foodItemId) {
        if (editFoodItem != null) {
            viewModel.setEditFoodItem(editFoodItem)
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSuccess()
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
            viewModel.saveFoodItem(store.foodStoreId)
        },
        onDeleteClick = {
            viewModel.deleteFoodItem(
                foodStoreId = store.foodStoreId,
                onDeleted = onSuccess
            )
        },
        onLocalImageSelected = viewModel::updateLocalImage
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
                        if (uiState.isEditMode) "Sửa món ăn" else "Thêm món ăn"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Thêm món cho quán:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = store.address ?: "Chưa có địa chỉ",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    imagePicker.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Chọn ảnh món từ thư viện")
            }

            val previewImage =
                uiState.localImageUri ?: uiState.imageUrl.takeIf { it.isNotBlank() }

            if (previewImage != null) {
                Spacer(modifier = Modifier.height(10.dp))

                AsyncImage(
                    model = previewImage,
                    contentDescription = "Ảnh món ăn",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tên món ăn") },
                leadingIcon = {
                    Icon(Icons.Default.RestaurantMenu, null)
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.price,
                onValueChange = onPriceChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Giá món ăn") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, null)
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.imageUrl,
                onValueChange = onImageUrlChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Link hình ảnh món") },
                leadingIcon = {
                    Icon(Icons.Default.Image, null)
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mô tả món ăn") },
                minLines = 3
            )

            uiState.error?.let {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSaveClick,
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.AddCircle, null)

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = when {
                        uiState.isSaving -> "Đang lưu..."
                        uiState.isEditMode -> "Cập nhật món ăn"
                        else -> "Lưu món ăn"
                    }
                )
            }

            if (uiState.isEditMode) {
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Xóa món ăn")
                }
            }
        }
    }
}