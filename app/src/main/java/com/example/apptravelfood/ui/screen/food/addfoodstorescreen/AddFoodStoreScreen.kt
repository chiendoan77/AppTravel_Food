package com.example.apptravelfood.ui.screen.food.addfoodstorescreen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.apptravelfood.core.untils.LocationHelper
import com.example.apptravelfood.core.untils.getFullAddressFromLocation
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.domain.model.AddressSuggestion
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong

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
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            LocationHelper(context).getCurrentLocation { lat, lng ->
                getFullAddressFromLocation(context, lat, lng) { fullAddress ->
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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) getCurrentAddress()
        else viewModel.setError("Bạn cần cấp quyền vị trí để dùng chức năng này")
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(context, "Đã lưu quán ăn thành công!", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
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
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (permission == PackageManager.PERMISSION_GRANTED) getCurrentAddress()
                else locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        },
        onDescriptionChange = viewModel::updateDescription,
        onSaveClick = {
            viewModel.saveFoodStore(
                context = context,
                place = place,
                userId = userId
            )
        },
        onLocalImageSelected = viewModel::updateLocalImage,
        onAddressSuggestionClick = viewModel::selectAddressSuggestion,
        onUseCurrentLocationClick = {
            val permission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permission == PackageManager.PERMISSION_GRANTED) getCurrentAddress()
            else locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    )
}

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
    var addressFocused by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> onLocalImageSelected(uri) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Scrollable body ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Space for fixed top bar
            Spacer(modifier = Modifier.height(64.dp))

            // Branding Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Travel & Food",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        brush = Brush.linearGradient(
                            colors = listOf(AppGreen, AppGreenStrong)
                        )
                    )
                )
            }

            // ── Photo picker ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color(0xFFF2F2F2))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.localImageUri != null) {
                    AsyncImage(
                        model = uiState.localImageUri,
                        contentDescription = "Ảnh quán",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // dark gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.4f)
                                    ),
                                    startY = 100f
                                )
                            )
                    )
                    // change photo chip
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
                            null,
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
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(AppGreenLight.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                null,
                                modifier = Modifier.size(34.dp),
                                tint = AppGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Thêm ảnh quán ăn", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            "Nên dùng ảnh rõ, tỉ lệ 16:9",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // ── Place context card ────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppGreenLight.copy(alpha = 0.14f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Place,
                        null,
                        tint = AppGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Thêm quán gần địa điểm", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            place.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppGreenStrong
                        )
                        if (!place.address.isNullOrBlank()) {
                            Text(place.address, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionLabel("Thông tin quán")

                Spacer(modifier = Modifier.height(10.dp))

                // Store name
                StoreTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = "Tên quán ăn",
                    placeholder = "Ví dụ: Bún bò Huế Dì Ba",
                    leadingIcon = Icons.Default.Store
                )

                Spacer(modifier = Modifier.height(12.dp))

                SectionLabel("Địa chỉ")

                Spacer(modifier = Modifier.height(10.dp))

                // Address field
                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = onAddressChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { addressFocused = it.isFocused },
                    label = { Text("Địa chỉ quán") },
                    placeholder = { Text("Nhập địa chỉ hoặc dùng GPS", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            tint = AppGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    minLines = 2,
                    shape = RoundedCornerShape(14.dp),
                    colors = storeFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Current location button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppGreenLight.copy(alpha = 0.18f))
                        .clickable { onUseCurrentLocationClick() }
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        null,
                        tint = AppGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Dùng vị trí hiện tại",
                        color = AppGreenStrong,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (uiState.isSearchingAddress) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = AppGreen
                    )
                }

                // Address suggestions dropdown
                if (addressFocused && uiState.addressSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            uiState.addressSuggestions.forEach { suggestion ->
                                ListItem(
                                    headlineContent = {
                                        Text(suggestion.address, fontSize = 13.sp)
                                    },
                                    supportingContent = {
                                        Text(
                                            "GPS: ${suggestion.latitude}, ${suggestion.longitude}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            null,
                                            tint = AppGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onAddressSuggestionClick(suggestion) }
                                )
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionLabel("Mô tả")

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                StoreTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    label = "Mô tả quán",
                    placeholder = "Phong cách, đặc sản, giờ mở cửa...",
                    leadingIcon = Icons.Default.Description,
                    minLines = 3
                )

                uiState.error?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

                Spacer(modifier = Modifier.height(18.dp))

                AppAccentButton(
                    text = if (uiState.isSaving) "Đang lưu..." else "Lưu quán ăn",
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !uiState.isSaving
                )

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
                text = "Đóng góp quán ăn",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppGreenStrong
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppGreen,
        letterSpacing = 0.4.sp
    )
}

@Composable
private fun StoreTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.LightGray) },
        leadingIcon = {
            Icon(leadingIcon, null, tint = AppGreen, modifier = Modifier.size(20.dp))
        },
        singleLine = minLines == 1,
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = storeFieldColors()
    )
}

@Composable
private fun storeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AppGreen,
    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f),
    focusedLabelColor = AppGreen,
    cursorColor = AppGreen
)