package com.patientrecords.doctorapp.ui.addpaitents.components

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.patientrecords.doctorapp.ui.theme.DashedBorderGreen
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PhotoPickerBackground
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPicker(
    selectedUri: Uri?,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemoved: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    // 📷 Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) tempImageUri?.let(onPhotoSelected)
    }

    // 🖼️ Gallery launcher (NO permission needed)
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(onPhotoSelected)
    }

    // 🔐 Camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            launchCamera(context) {
                tempImageUri = it
                cameraLauncher.launch(it)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()) {

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {

            if (selectedUri != null) {

                Box(contentAlignment = Alignment.TopEnd) {

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(selectedUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { if (!isLoading) showBottomSheet = true }
                    )

                    if (!isLoading) {
                        IconButton(
                            onClick = onPhotoRemoved,
                            modifier = Modifier
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }

            } else {
                DashedCirclePlaceholder(
                    isLoading = isLoading,
                    onClick = { showBottomSheet = true }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (selectedUri != null) "Change Photo" else "Add Photo",
            fontWeight = FontWeight.Medium
        )
    }

    // ================= Bottom Sheet =================

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            PhotoSourceBottomSheet(
                onCameraClick = {
                    scope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                    if (hasCameraPermission) {
                        launchCamera(context) {
                            tempImageUri = it
                            cameraLauncher.launch(it)
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onGalleryClick = {
                    scope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }
}

/* -------------------- UI Components -------------------- */

@Composable
private fun DashedCirclePlaceholder(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(PhotoPickerBackground)
            .dashedBorder(
                DashedBorderGreen,
                2.dp,
                8.dp,
                6.dp,
                CircleShape
            )
            .clickable(enabled = !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = PrimaryGreen)
        } else {
            Icon(
                Icons.Outlined.CameraAlt,
                null,
                tint = PrimaryGreen,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun PhotoSourceBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(Modifier
        .fillMaxWidth()
        .padding(24.dp)) {

        Text("Select Photo", fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

            PhotoSourceOption(Icons.Outlined.CameraAlt, "Camera", onCameraClick)
            PhotoSourceOption(Icons.Outlined.Image, "Gallery", onGalleryClick)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PhotoSourceOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(PhotoPickerBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label)
    }
}

/* -------------------- Utils -------------------- */

private fun launchCamera(context: Context, onUri: (Uri) -> Unit) {
    val file = File.createTempFile(
        "patient_${System.currentTimeMillis()}",
        ".jpg",
        context.cacheDir
    )
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    onUri(uri)
}

fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp,
    dashLength: Dp,
    gapLength: Dp,
    shape: Shape
) = this.then(
    Modifier.drawBehind {
        drawCircle(
            color = color,
            radius = size.minDimension / 2,
            style = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(dashLength.toPx(), gapLength.toPx())
                )
            )
        )
    }
)

/* -------------------- Preview -------------------- */

@Preview(showBackground = true)
@Composable
private fun PreviewPhotoPicker() {
    HealthcarePatientTheme {
        PhotoPicker(
            selectedUri = null,
            onPhotoSelected = {},
            onPhotoRemoved = {}
        )
    }
}
