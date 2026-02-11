package com.patientrecords.doctorapp.patientdetils.ui.screens.notes

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.patientdetils.ui.components.LoadingOverlay
import com.patientrecords.doctorapp.patientdetils.data.DoctorNotesUiState
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import com.patientrecords.doctorapp.ui.theme.TextPrimary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorNotesScreen(
    uiState: DoctorNotesUiState,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Doctor Notes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Back",
                            tint = PrimaryGreen
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSave,
                        enabled = uiState.content.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Save",
                            tint = if (uiState.content.isNotBlank()) PrimaryGreen
                            else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NotesToolbar(
                onBulletList = { /* Insert bullet point */ },
                onInsertImage = { /* Insert image */ },
                onVoiceInput = { /* Start voice input */ },
                onDone = onSave
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Last Edited Timestamp
            if (uiState.lastEditedAt != null) {
                Text(
                    text = "Last edited ${formatLastEdited(uiState.lastEditedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // Notes Editor
            NotesEditor(
                content = uiState.content,
                onContentChange = onContentChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            )
        }

        // Loading Overlay
        LoadingOverlay(isLoading = uiState.isSaving)
    }
}

@Composable
private fun NotesEditor(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        BasicTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
            ),
            cursorBrush = SolidColor(PrimaryGreen),
            decorationBox = { innerTextField ->
                Box {
                    if (content.isEmpty()) {
                        Text(
                            text = "Start typing your notes...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun NotesToolbar(
    onBulletList: () -> Unit,
    onInsertImage: () -> Unit,
    onVoiceInput: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ToolbarButton(
                    icon = Icons.Outlined.FormatListBulleted,
                    contentDescription = "Bullet list",
                    onClick = onBulletList
                )
                ToolbarButton(
                    icon = Icons.Outlined.Image,
                    contentDescription = "Insert image",
                    onClick = onInsertImage
                )
                ToolbarButton(
                    icon = Icons.Outlined.Mic,
                    contentDescription = "Voice input",
                    onClick = onVoiceInput
                )
            }

            TextButton(onClick = onDone) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatLastEdited(timestamp: String): String {
    return try {
        val dateTime = LocalDateTime.parse(timestamp)
        val formatter = DateTimeFormatter.ofPattern("'today at' h:mm a")
        dateTime.format(formatter)
    } catch (e: Exception) {
        timestamp
    }
}

// ==================== ViewModel ====================

/*
class DoctorNotesViewModel(
    private val patientId: String,
    private val visitId: String?,
    private val getDoctorNotesUseCase: GetDoctorNotesUseCase,
    private val saveDoctorNotesUseCase: SaveDoctorNotesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DoctorNotesUiState())
    val uiState: StateFlow<DoctorNotesUiState> = _uiState.asStateFlow()
    
    init {
        loadNotes()
    }
    
    private fun loadNotes() {
        viewModelScope.launch {
            getDoctorNotesUseCase(patientId, visitId)
                .onSuccess { note ->
                    _uiState.update { 
                        it.copy(
                            content = note?.content ?: "",
                            lastEditedAt = note?.lastEditedAt
                        )
                    }
                }
        }
    }
    
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun saveNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            saveDoctorNotesUseCase(
                patientId = patientId,
                visitId = visitId,
                content = _uiState.value.content
            ).fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isSaving = false, 
                            isSaved = true,
                            lastEditedAt = LocalDateTime.now().toString()
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSaving = false, error = error.message) }
                }
            )
        }
    }
}
*/

// ==================== Previews ====================

@Preview(showBackground = true)
@Composable
private fun DoctorNotesScreenPreview() {
    HealthcarePatientTheme {
        DoctorNotesScreen(
            uiState = DoctorNotesUiState(
                content = "Patient presents with mild fatigue and recurring headaches specifically in the frontal region.\n\nSymptoms appear to worsen in the late afternoon. BP 120/80. No signs of fever.\n\nPrescribed rest and hydration. Follow up in 3 days if symptoms persist.",
                lastEditedAt = "2023-10-24T10:45:00"
            ),
            onNavigateBack = {},
            onSave = {},
            onContentChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DoctorNotesScreenEmptyPreview() {
    HealthcarePatientTheme {
        DoctorNotesScreen(
            uiState = DoctorNotesUiState(
                content = "",
                lastEditedAt = null
            ),
            onNavigateBack = {},
            onSave = {},
            onContentChange = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DoctorNotesScreenDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        DoctorNotesScreen(
            uiState = DoctorNotesUiState(
                content = "Initial consultation notes:\n\nPatient reports chronic back pain for the past 2 weeks. No recent injuries. Pain level 6/10.\n\nRecommended: Physical therapy evaluation.",
                lastEditedAt = "2023-10-24T14:30:00"
            ),
            onNavigateBack = {},
            onSave = {},
            onContentChange = {}
        )
    }
}
