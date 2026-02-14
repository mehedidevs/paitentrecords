package com.patientrecords.doctorapp.addpaitents.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.ui.theme.BorderLight
import com.patientrecords.doctorapp.ui.theme.ErrorRed
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import com.patientrecords.doctorapp.ui.theme.TextSecondary


/**
 * Gender selector component with chip-style buttons
 * - Male / Female options
 * - Selected state highlight
 * - Animation on selection
 *
 * @param selectedGender Currently selected gender (null if none)
 * @param onGenderSelected Callback when gender is selected
 * @param error Error message (null if no error)
 * @param enabled Whether selector is enabled
 * @param modifier Modifier for the component
 */
@Composable
fun GenderSelector(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit,
    error: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val hasError = error != null

    Column(modifier = modifier) {
        // Label
        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gender chips row
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            GenderChip(
                label = "Male",
                isSelected = selectedGender == Gender.MALE,
                onClick = { onGenderSelected(Gender.MALE) },
                enabled = enabled,
                hasError = hasError && selectedGender == null
            )

            Spacer(modifier = Modifier.width(12.dp))

            GenderChip(
                label = "Female",
                isSelected = selectedGender == Gender.FEMALE,
                onClick = { onGenderSelected(Gender.FEMALE) },
                enabled = enabled,
                hasError = hasError && selectedGender == null
            )
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
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Individual gender selection chip
 */
@Composable
private fun GenderChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> PrimaryGreen.copy(alpha = 0.1f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(200),
        label = "chipBackground"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            hasError -> ErrorRed
            isSelected -> PrimaryGreen
            else -> BorderLight
        },
        animationSpec = tween(200),
        label = "chipBorder"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> PrimaryGreen
            else -> TextSecondary
        },
        animationSpec = tween(200),
        label = "chipText"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },

                onClick = onClick
            )
            .padding(horizontal = 32.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = textColor
        )
    }
}

// ============== PREVIEWS ==============

@Preview(showBackground = true)
@Composable
private fun GenderSelectorNoneSelectedPreview() {
    HealthcarePatientTheme {
        GenderSelector(
            selectedGender = null,
            onGenderSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GenderSelectorMaleSelectedPreview() {
    HealthcarePatientTheme {
        GenderSelector(
            selectedGender = Gender.MALE,
            onGenderSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GenderSelectorFemaleSelectedPreview() {
    HealthcarePatientTheme {
        GenderSelector(
            selectedGender = Gender.FEMALE,
            onGenderSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GenderSelectorErrorPreview() {
    HealthcarePatientTheme {
        GenderSelector(
            selectedGender = null,
            onGenderSelected = {},
            error = "Please select a gender",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun GenderSelectorDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        GenderSelector(
            selectedGender = Gender.MALE,
            onGenderSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
