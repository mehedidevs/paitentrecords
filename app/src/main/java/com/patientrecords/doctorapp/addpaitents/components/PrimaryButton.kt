package com.patientrecords.doctorapp.addpaitents.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen


/**
 * Primary action button with:
 * - Loading state indicator
 * - Optional leading/trailing icons
 * - Full width option
 * - Customizable colors
 *
 * @param text Button label text
 * @param onClick Click callback
 * @param isLoading Show loading indicator
 * @param enabled Whether button is enabled
 * @param leadingIcon Optional icon before text
 * @param trailingIcon Optional icon after text
 * @param fullWidth Whether button should fill max width
 * @param height Button height
 * @param backgroundColor Background color
 * @param contentColor Content (text/icon) color
 * @param modifier Modifier for the component
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    fullWidth: Boolean = true,
    height: Dp = 56.dp,
    backgroundColor: Color = PrimaryGreen,
    contentColor: Color = Color.White,
    disabledBackgroundColor: Color = PrimaryGreen.copy(alpha = 0.5f),
    disabledContentColor: Color = Color.White.copy(alpha = 0.7f),
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = disabledBackgroundColor,
            disabledContentColor = disabledContentColor
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height)
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                    fadeOut(animationSpec = tween(200))
            },
            label = "buttonContent"
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    color = contentColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leadingIcon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                    
                    trailingIcon?.let { icon ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Secondary/outline button variant
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    fullWidth: Boolean = true,
    height: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    PrimaryButton(
        text = text,
        onClick = onClick,
        isLoading = isLoading,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        fullWidth = fullWidth,
        height = height,
        backgroundColor = Color.Transparent,
        contentColor = PrimaryGreen,
        disabledBackgroundColor = Color.Transparent,
        disabledContentColor = PrimaryGreen.copy(alpha = 0.5f),
        modifier = modifier
    )
}

// ============== PREVIEWS ==============

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonDefaultPreview() {
    HealthcarePatientTheme {
        PrimaryButton(
            text = "Save Patient",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonWithIconPreview() {
    HealthcarePatientTheme {
        PrimaryButton(
            text = "Save Patient",
            onClick = {},
            trailingIcon = Icons.Default.Check,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonLoadingPreview() {
    HealthcarePatientTheme {
        PrimaryButton(
            text = "Save Patient",
            onClick = {},
            isLoading = true,
            trailingIcon = Icons.Default.Check,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonDisabledPreview() {
    HealthcarePatientTheme {
        PrimaryButton(
            text = "Save Patient",
            onClick = {},
            enabled = false,
            trailingIcon = Icons.Default.Check,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SecondaryButtonPreview() {
    HealthcarePatientTheme {
        SecondaryButton(
            text = "Cancel",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PrimaryButtonDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        PrimaryButton(
            text = "Save Patient",
            onClick = {},
            trailingIcon = Icons.Default.Check,
            modifier = Modifier.padding(16.dp)
        )
    }
}
