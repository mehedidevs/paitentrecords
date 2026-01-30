package com.patientrecords.doctorapp.ui.screens.addpaitents.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.ui.theme.BorderLight
import com.patientrecords.doctorapp.ui.theme.ErrorRed
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import com.patientrecords.doctorapp.ui.theme.TextSecondary
import com.patientrecords.doctorapp.ui.theme.TextTertiary

/**
 * Multiline address input field component
 * - Optional field indicator
 * - Multiline text input
 * - Character count (optional)
 *
 * @param value Current address value
 * @param onValueChange Callback when value changes
 * @param error Error message (null if no error)
 * @param enabled Whether field is enabled
 * @param minHeight Minimum height of the field
 * @param maxLength Maximum character length
 * @param showCharCount Whether to show character count
 * @param modifier Modifier for the component
 */
@Composable
fun AddressField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
    enabled: Boolean = true,
    minHeight: Dp = 100.dp,
    maxLength: Int = 500,
    showCharCount: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val hasError = error != null

    val borderColor = when {
        hasError -> ErrorRed
        isFocused -> PrimaryGreen
        else -> BorderLight
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label row with optional indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Address",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Optional",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input field container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= maxLength) {
                        onValueChange(newValue)
                    }
                },
                enabled = enabled,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = false,
                cursorBrush = SolidColor(PrimaryGreen),
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "Street, City...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Character count and error
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 4.dp, end = 4.dp)
        ) {
            // Error message
            AnimatedVisibility(
                visible = hasError,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed
                )
            }

            if (!hasError) {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Character count
            if (showCharCount) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${value.length}/$maxLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (value.length >= maxLength) ErrorRed else TextTertiary
                )
            }
        }
    }
}

// ============== PREVIEWS ==============

@Preview(showBackground = true)
@Composable
private fun AddressFieldEmptyPreview() {
    HealthcarePatientTheme {
        AddressField(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddressFieldFilledPreview() {
    HealthcarePatientTheme {
        AddressField(
            value = "123 Main Street, Apartment 4B\nNew York, NY 10001",
            onValueChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddressFieldWithCharCountPreview() {
    HealthcarePatientTheme {
        AddressField(
            value = "123 Main Street",
            onValueChange = {},
            showCharCount = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddressFieldErrorPreview() {
    HealthcarePatientTheme {
        AddressField(
            value = "A".repeat(501),
            onValueChange = {},
            error = "Address cannot exceed 500 characters",
            showCharCount = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AddressFieldDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        AddressField(
            value = "456 Oak Avenue\nLos Angeles, CA 90001",
            onValueChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
