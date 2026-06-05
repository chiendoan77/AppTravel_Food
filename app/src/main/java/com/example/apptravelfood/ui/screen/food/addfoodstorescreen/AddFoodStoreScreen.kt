package com.example.apptravelfood.ui.screen.food.addfoodstorescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apptravelfood.data.remote.dto.LocalResultsDto

@Composable
fun AddFoodStoreRoute(
    viewModel: AddFoodStoreViewModel,
    place: LocalResultsDto,
    userId: Long,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSuccess()
        }
    }

    AddFoodStoreScreen(
        uiState = uiState,
        place = place,
        onBack = onBack,
        onNameChange = viewModel::updateName,
        onAddressChange = viewModel::updateAddress,
        onDescriptionChange = viewModel::updateDescription,
        onImageUrlChange = viewModel::updateImageUrl,
        onSaveClick = {
            val placeId = place.place_id ?: return@AddFoodStoreScreen

            viewModel.saveFoodStore(
                placeId = placeId,
                userId = userId
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodStoreScreen(
    uiState: AddFoodStoreUiState,
    place: LocalResultsDto,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đóng góp quán ăn") },
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
                        text = "Thêm quán gần:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = place.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = place.address ?: "Chưa có địa chỉ",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tên quán ăn") },
                leadingIcon = { Icon(Icons.Default.Store, null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.address,
                onValueChange = onAddressChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Địa chỉ quán") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                singleLine = false,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.imageUrl,
                onValueChange = onImageUrlChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Link hình ảnh quán") },
                leadingIcon = { Icon(Icons.Default.Image, null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mô tả quán") },
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
                Icon(Icons.Default.AddBusiness, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isSaving) {
                        "Đang lưu..."
                    } else {
                        "Lưu quán ăn"
                    }
                )
            }
        }
    }
}