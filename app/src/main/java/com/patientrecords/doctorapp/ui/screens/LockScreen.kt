package com.patientrecords.doctorapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.patientrecords.doctorapp.ui.components.StatusBar
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme

@Composable
fun LockScreen(
    userName: String = "Dr. Smith",
    userAvatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuCuJNy5sQ1kxplpXF59Jruf97KO34m8AgGPwKUNvioZ7jWA8KW9Otwbma50trsjzk1uOKkE-mCJANou7Ht5MsEGC0GFcaqmX1BRSRhEGtPNgcLLaHu4XYbQaL18vWYODtotFiPBcDldQ52bUhyhi6gSg9cgh8POjQo5DtZw6JqcSn08C5IqeOr3Cg_0ws7M1GXQm43lXVJGtp5KZHwLjpriM4uwz5WHfPZo2Y-pqaebMY2YrbVMiFHKOpgdreVMrMw-pmqJbO86jVQ",
    onPinComplete: (String) -> Unit = {},
    onFingerprintClick: () -> Unit = {},
    onForgotPinClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar
            StatusBar(time = "9:42")

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // User Avatar
                    AsyncImage(
                        model = userAvatarUrl,
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Welcome Text
                    Text(
                        text = "Welcome Back,\n$userName",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Instruction
                    Text(
                        text = "Enter PIN to unlock",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // PIN Indicators
                PinIndicators(
                    pinLength = 4,
                    currentLength = pinInput.length,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Numeric Keypad
                NumericKeypad(
                    onNumberClick = { number ->
                        if (pinInput.length < 4) {
                            pinInput += number
                            if (pinInput.length == 4) {
                                onPinComplete(pinInput)
                                // Reset after completion
                                pinInput = ""
                            }
                        }
                    },
                    onBackspaceClick = {
                        if (pinInput.isNotEmpty()) {
                            pinInput = pinInput.dropLast(1)
                        }
                    },
                    onFingerprintClick = onFingerprintClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Forgot PIN
                TextButton(
                    onClick = onForgotPinClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Forgot PIN?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PinIndicators(
    pinLength: Int,
    currentLength: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
        repeat(pinLength) { index ->
            PinDot(isFilled = index < currentLength)
        }
    }
}

@Composable
private fun PinDot(
    isFilled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(
                if (isFilled) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )
    ) {
        if (!isFilled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Transparent)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {}
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                border = BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary
                )
            ) {}
        }
    }
}

@Composable
private fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onFingerprintClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Row 1: 1, 2, 3
        KeypadRow(
            numbers = listOf("1", "2", "3"),
            onNumberClick = onNumberClick
        )

        // Row 2: 4, 5, 6
        KeypadRow(
            numbers = listOf("4", "5", "6"),
            onNumberClick = onNumberClick
        )

        // Row 3: 7, 8, 9
        KeypadRow(
            numbers = listOf("7", "8", "9"),
            onNumberClick = onNumberClick
        )

        // Row 4: Fingerprint, 0, Backspace
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fingerprint Button
            IconButton(
                onClick = onFingerprintClick,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Fingerprint,
                    contentDescription = "Fingerprint",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // 0 Button
            KeypadButton(
                number = "0",
                onClick = { onNumberClick("0") }
            )

            // Backspace Button
            IconButton(
                onClick = onBackspaceClick,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Backspace,
                    contentDescription = "Backspace",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun KeypadRow(
    numbers: List<String>,
    onNumberClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
        numbers.forEach { number ->
            KeypadButton(
                number = number,
                onClick = { onNumberClick(number) }
            )
        }
    }
}

@Composable
private fun KeypadButton(
    number: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .size(80.dp)
            .aspectRatio(1f),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(name = "Lock Screen - Light", showBackground = true)
@Composable
private fun LockScreenPreviewLight() {
    HealthcarePatientTheme(darkTheme = false) {
        LockScreen()
    }
}

@Preview(name = "Lock Screen - Dark", showBackground = true)
@Composable
private fun LockScreenPreviewDark() {
    HealthcarePatientTheme(darkTheme = true) {
        LockScreen()
    }
}

@Preview(name = "Lock Screen - With PIN", showBackground = true)
@Composable
private fun LockScreenPreviewWithPin() {
    HealthcarePatientTheme(darkTheme = false) {
        LockScreen()
    }
}
