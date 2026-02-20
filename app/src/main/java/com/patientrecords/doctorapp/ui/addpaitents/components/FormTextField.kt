package com.patientrecords.doctorapp.ui.addpaitents.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.ui.theme.BorderLight
import com.patientrecords.doctorapp.ui.theme.ErrorRed
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import com.patientrecords.doctorapp.ui.theme.TextSecondary

/**
 * Reusable form text field component with:
 * - Label with optional required indicator
 * - Placeholder text
 * - Trailing icon support
 * - Error state and message
 * - Focus highlighting
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param label Field label text
 * @param placeholder Placeholder text when empty
 * @param isRequired Show required asterisk indicator
 * @param error Error message (null if no error)
 * @param trailingIcon Optional trailing icon
 * @param keyboardType Keyboard type for input
 * @param imeAction IME action button type
 * @param singleLine Whether field is single line
 * @param maxLength Maximum character length
 * @param enabled Whether field is enabled
 * @param modifier Modifier for the component
 */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isRequired: Boolean = false,
    error: String? = null,
    trailingIcon: ImageVector? = null,
    trailingIconTint: Color = PrimaryGreen,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    maxLength: Int? = null,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
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
        // Label row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (isRequired) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ErrorRed
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Input field container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (maxLength == null || newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    enabled = enabled,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        onDone = { focusManager.clearFocus() }
                    ),
                    singleLine = singleLine,
                    cursorBrush = SolidColor(PrimaryGreen),
                    interactionSource = interactionSource,
                    visualTransformation = visualTransformation,
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSecondary
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                
                // Trailing icon
                if (trailingIcon != null) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = trailingIconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        // Error message
        AnimatedVisibility(
            visible = hasError,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

// ============== PREVIEWS ==============

@Preview(showBackground = true)
@Composable
private fun FormTextFieldEmptyPreview() {
    HealthcarePatientTheme {
        FormTextField(
            value = "",
            onValueChange = {},
            label = "Full name",
            placeholder = "e.g. John Doe",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormTextFieldFilledPreview() {
    HealthcarePatientTheme {
        FormTextField(
            value = "John Doe",
            onValueChange = {},
            label = "Full name",
            placeholder = "e.g. John Doe",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormTextFieldRequiredPreview() {
    HealthcarePatientTheme {
        FormTextField(
            value = "",
            onValueChange = {},
            label = "Mobile number",
            placeholder = "10 digit number",
            isRequired = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormTextFieldErrorPreview() {
    HealthcarePatientTheme {
        FormTextField(
            value = "123",
            onValueChange = {},
            label = "Mobile number",
            placeholder = "10 digit number",
            isRequired = true,
            error = "Mobile number must be 10 digits",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun FormTextFieldDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        FormTextField(
            value = "",
            onValueChange = {},
            label = "Full name",
            placeholder = "e.g. John Doe",
            modifier = Modifier.padding(16.dp)
        )
    }
}
