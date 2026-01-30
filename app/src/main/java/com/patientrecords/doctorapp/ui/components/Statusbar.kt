package com.patientrecords.doctorapp.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.NetworkCell
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme


@Composable
fun StatusBar(
    time: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .alpha(0.6f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Time
        Text(
            text = time,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // System Icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.NetworkCell,
                contentDescription = "Signal",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(16.dp)
            )
            Icon(
                imageVector = Icons.Outlined.Wifi,
                contentDescription = "WiFi",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(16.dp)
            )
            Icon(
                imageVector = Icons.Outlined.BatteryFull,
                contentDescription = "Battery",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(name = "Status Bar - Light", showBackground = true)
@Composable
private fun StatusBarPreviewLight() {
    HealthcarePatientTheme(darkTheme = false) {
        StatusBar(time = "9:41")
    }
}

@Preview(name = "Status Bar - Dark", showBackground = true)
@Composable
private fun StatusBarPreviewDark() {
    HealthcarePatientTheme(darkTheme = true) {
        StatusBar(time = "9:41")
    }
}