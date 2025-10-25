package com.ghostdev.storekeeperhng.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghostdev.storekeeperhng.presentation.addedit.AddEditViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    productId: Long?,
    onBack: () -> Unit,
    vm: AddEditViewModel = koinViewModel(parameters = { parametersOf(productId) })
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) onBack()
    }

    val snackbarHostState = androidx.compose.runtime.remember { androidx.compose.material3.SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditMode) "Edit Product" else "New Product",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    if (state.isValid && !state.isSaving) {
                        IconButton(
                            onClick = { vm.save() },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(Color.Black, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Save",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        FormContent(paddingValues = padding, state = state, vm = vm, snackbarHostState = snackbarHostState)
    }
}

@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
@Composable
private fun FormContent(
    paddingValues: PaddingValues,
    state: com.ghostdev.storekeeperhng.presentation.addedit.AddEditUiState,
    vm: AddEditViewModel,
    snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Launchers
    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = com.ghostdev.storekeeperhng.util.ImageUtils.compressAndSave(context, uri)
            if (path != null) vm.onImagePathChange(path)
        }
    }

    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val baos = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, baos)
            val bytes = baos.toByteArray()
            val path = com.ghostdev.storekeeperhng.util.ImageUtils.compressAndSave(context, bytes)
            vm.onImagePathChange(path)
        }
    }

    // Permissions
    val cameraPermission = com.google.accompanist.permissions.rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val galleryPermissionName = if (android.os.Build.VERSION.SDK_INT >= 33) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val galleryPermission = com.google.accompanist.permissions.rememberPermissionState(
        galleryPermissionName
    )

    // Track pending actions so when a permission is granted we immediately proceed
    val pendingCamera = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val pendingGallery = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // React to camera permission result
    androidx.compose.runtime.LaunchedEffect(cameraPermission.status) {
        if (pendingCamera.value) {
            when (cameraPermission.status) {
                is com.google.accompanist.permissions.PermissionStatus.Granted -> {
                    pendingCamera.value = false
                    cameraLauncher.launch(null)
                }
                is com.google.accompanist.permissions.PermissionStatus.Denied -> {
                    pendingCamera.value = false
                    snackbarHostState.showSnackbar("Camera permission is required to take a photo")
                }
            }
        }
    }

    // React to gallery permission result
    androidx.compose.runtime.LaunchedEffect(galleryPermission.status) {
        if (pendingGallery.value) {
            when (galleryPermission.status) {
                is com.google.accompanist.permissions.PermissionStatus.Granted -> {
                    pendingGallery.value = false
                    galleryLauncher.launch("image/*")
                }
                is com.google.accompanist.permissions.PermissionStatus.Denied -> {
                    pendingGallery.value = false
                    snackbarHostState.showSnackbar("Storage permission is required to pick an image")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(24.dp))

        // Image Upload Section
        Text(
            "Product Image",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp
            ),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF9FAFB))
            ) {
                if (state.imagePath.isNullOrBlank()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFE5E7EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No image selected",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    val req = coil.request.ImageRequest.Builder(context)
                        .data(state.imagePath)
                        .memoryCacheKey("${state.imagePath}-${state.imageCacheBust}")
                        .build()
                    coil.compose.AsyncImage(
                        model = req,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                state.imagePath?.let { path ->
                                    if (com.ghostdev.storekeeperhng.util.ImageUtils.rotateInPlace(path, 90f)) {
                                        vm.onImageRotated()
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.RotateRight,
                                contentDescription = "Rotate",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { vm.onImagePathChange(null) },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Remove",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = {
                    when (cameraPermission.status) {
                        is com.google.accompanist.permissions.PermissionStatus.Granted -> {
                            cameraLauncher.launch(null)
                        }
                        is com.google.accompanist.permissions.PermissionStatus.Denied -> {
                            pendingCamera.value = true
                            cameraPermission.launchPermissionRequest()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5E7EB))
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Camera", fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = {
                    when (galleryPermission.status) {
                        is com.google.accompanist.permissions.PermissionStatus.Granted -> {
                            galleryLauncher.launch("image/*")
                        }
                        is com.google.accompanist.permissions.PermissionStatus.Denied -> {
                            pendingGallery.value = true
                            galleryPermission.launchPermissionRequest()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Gallery", fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(32.dp))

        // Product Name
        FormField(
            label = "Product Name",
            value = state.name,
            onValueChange = vm::onNameChange,
            placeholder = "Enter product name",
            error = state.nameError?.message,
            required = true
        )

        Spacer(Modifier.height(20.dp))

        // Quantity and Price Row
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                FormField(
                    label = "Quantity",
                    value = state.quantityText,
                    onValueChange = vm::onQuantityChange,
                    placeholder = "0",
                    error = state.quantityError?.message,
                    keyboardType = KeyboardType.Number,
                    required = true
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                FormField(
                    label = "Price (₦)",
                    value = state.price,
                    onValueChange = vm::onPriceChange,
                    placeholder = "0.00",
                    error = state.priceError?.message,
                    keyboardType = KeyboardType.Decimal,
                    required = true
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // SKU
        FormField(
            label = "SKU",
            value = state.sku,
            onValueChange = vm::onSkuChange,
            placeholder = "Stock Keeping Unit",
            required = false
        )

        Spacer(Modifier.height(40.dp))

        // Save Button
        Button(
            onClick = { vm.save() },
            enabled = state.isValid && !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            )
        ) {
            Text(
                if (state.isEditMode) "Update Product" else "Save Product",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp
                )
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    required: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp
                ),
                color = Color.Black
            )
            if (required) {
                Text(
                    " *",
                    color = Color(0xFFEF4444),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(if (error != null) 0.dp else 1.dp, RoundedCornerShape(12.dp)),
            placeholder = {
                Text(
                    placeholder,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color(0xFFFEE2E2),
                focusedIndicatorColor = if (error != null) Color(0xFFEF4444) else Color.Black,
                unfocusedIndicatorColor = if (error != null) Color(0xFFEF4444) else Color(0xFFE5E7EB),
                errorIndicatorColor = Color(0xFFEF4444),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}