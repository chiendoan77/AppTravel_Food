package com.example.apptravelfood.ui.screen.food.addfoodstorescreen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.apptravelfood.core.untils.LocationHelper
import com.example.apptravelfood.core.untils.getFullAddressFromLocation
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.domain.model.AddressSuggestion
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppPageSurface

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AddFoodStoreRoute(
    viewModel: AddFoodStoreViewModel,
    place: LocalResultsDto,
    userId: Long,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val getCurrentAddress = {
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            LocationHelper(context).getCurrentLocation { lat, lng ->
                getFullAddressFromLocation(
                    context = context,
                    latitude = lat,
                    longitude = lng
                ) { fullAddress ->
                    viewModel.updateCurrentLocationSafe(
                        address = fullAddress,
                        latitude = lat,
                        longitude = lng
                    )
                }
            }
        } else {
            viewModel.setError("Chưa cấp quyền vị trí")
        }
    }
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                getCurrentAddress()
            } else {
                viewModel.setError("Bạn cần cấp quyền vị trí để dùng chức năng này")
            }
        }
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
        onAddressChange = { value ->

            viewModel.updateAddress(value)

            if (value.trim().length == 12) {

                val permission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if (permission == PackageManager.PERMISSION_GRANTED) {
                    getCurrentAddress()
                } else {
                    locationPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
        },
        onDescriptionChange = viewModel::updateDescription,
        onSaveClick = {
            val placeId = place.place_id ?: return@AddFoodStoreScreen

            viewModel.saveFoodStore(
                context = context,
                placeId = placeId,
                userId = userId
            )
        },
        onLocalImageSelected = viewModel::updateLocalImage,
        onAddressSuggestionClick = viewModel::selectAddressSuggestion,
        onUseCurrentLocationClick = {
            val permission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                getCurrentAddress()
            } else {
                locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
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
    onSaveClick: () -> Unit,
    onLocalImageSelected: (Uri?) -> Unit,
    onAddressSuggestionClick: (AddressSuggestion) -> Unit,
    onUseCurrentLocationClick: () -> Unit
) {
    var addressFocused by remember {
        mutableStateOf(false)
    }
    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            onLocalImageSelected(uri)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đóng góp quán ăn") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        AppPageSurface(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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

                OutlinedButton(
                    onClick = {
                        imagePicker.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppGreen
                    ),
                    border = BorderStroke(1.dp, AppGreen)
                ) {
                    Icon(Icons.Default.Image, null, tint = AppGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chọn ảnh quán")
                }
                if (uiState.localImageUri != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        AsyncImage(
                            model = uiState.localImageUri,
                            contentDescription = "Ảnh quán preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            addressFocused = it.isFocused
                        },
                    label = { Text("Địa chỉ quán") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onUseCurrentLocationClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppGreen
                    ),
                    border = BorderStroke(1.dp, AppGreen)
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = AppGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dùng vị trí hiện tại")
                }

                if (uiState.isSearchingAddress) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (addressFocused && uiState.addressSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column {
                            uiState.addressSuggestions.forEach { suggestion ->
                                ListItem(
                                    headlineContent = {
                                        Text(suggestion.address)
                                    },
                                    supportingContent = {
                                        Text("GPS: ${suggestion.latitude}, ${suggestion.longitude}")
                                    },
                                    leadingContent = {
                                        Icon(Icons.Default.LocationOn, null)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onAddressSuggestionClick(suggestion)
                                        }
                                )

                                HorizontalDivider()
                            }
                        }
                    }
                }

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

                AppAccentButton(
                    text = if (uiState.isSaving) "Đang lưu..." else "Lưu quán ăn",
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isSaving
                )
            }
        }
    }
}