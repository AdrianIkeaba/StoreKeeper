package com.ghostdev.storekeeperhng.presentation.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(padding: androidx.compose.foundation.layout.PaddingValues, onResetToSplash: () -> Unit) {
    val prefs = get<com.ghostdev.storekeeperhng.data.prefs.ProfilePrefs>()
    val name by prefs.userName.collectAsState(initial = "")
    val store by prefs.storeName.collectAsState(initial = "")
    val imagePath by prefs.profileImagePath.collectAsState(initial = null)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current

    val galleryPermissionName = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
    val galleryPermission = rememberPermissionState(galleryPermissionName)
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Launchers
    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = com.ghostdev.storekeeperhng.util.ImageUtils.compressAndSave(context, uri)
            prefs.setProfileImagePath(path)
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
            prefs.setProfileImagePath(path)
        }
    }

    var localName by remember { mutableStateOf("") }
    var localStore by remember { mutableStateOf("") }
    var showDelete by remember { mutableStateOf(false) }

    LaunchedEffect(name, store) {
        localName = name
        localStore = store
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
            color = Color.Black
        )
        Spacer(Modifier.height(24.dp))

        // Profile image
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                if (imagePath.isNullOrBlank()) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                } else {
                    AsyncImage(model = imagePath, contentDescription = null, modifier = Modifier.fillMaxSize())
                }
            }
            Spacer(Modifier.width(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        when (cameraPermission.status) {
                            is PermissionStatus.Granted -> cameraLauncher.launch(null)
                            is PermissionStatus.Denied -> cameraPermission.launchPermissionRequest()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5E7EB))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Camera")
                }
                Button(
                    onClick = {
                        when (galleryPermission.status) {
                            is PermissionStatus.Granted -> galleryLauncher.launch("image/*")
                            is PermissionStatus.Denied -> galleryPermission.launchPermissionRequest()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(text = "Your Name", color = Color.Black, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        TextField(
            value = localName,
            onValueChange = { localName = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color(0xFFE5E7EB),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(Modifier.height(16.dp))

        Text(text = "Store Name", color = Color.Black, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        TextField(
            value = localStore,
            onValueChange = { localStore = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color(0xFFE5E7EB),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                prefs.setUserName(localName.trim())
                prefs.setStoreName(localStore.trim())
                scope.launch { snackbarHostState.showSnackbar("Saved") }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
        ) {
            Text("Save Changes", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        }

        Spacer(Modifier.height(32.dp))

        Text("Danger Zone", color = Color(0xFFEF4444), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { showDelete = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5))
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
            Spacer(Modifier.width(8.dp))
            Text("Delete Store & Reset App", color = Color(0xFFEF4444), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        }

        SnackbarHost(hostState = snackbarHostState)
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            confirmButton = {
                Button(
                    onClick = {
                        prefs.resetAll(context)
                        showDelete = false
                        onResetToSplash()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDelete = false }, shape = RoundedCornerShape(12.dp)) { Text("Cancel", color = Color.Black) }
            },
            title = { Text("Delete Store?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.Black) },
            text = { Text("This removes all data and resets the app to first launch.", color = Color.Gray) },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
