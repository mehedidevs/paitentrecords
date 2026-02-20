package com.patientrecords.doctorapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.patientrecords.doctorapp.domain.models.Patient
import com.patientrecords.doctorapp.ui.theme.Primary
import com.patientrecords.doctorapp.ui.theme.TextPrimary

@Composable
fun PatientAvatar(patient: Patient) {


    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Primary),
        contentAlignment = Alignment.Center
    ) {

        Log.d("TAG", "PatientAvatar: ${patient.photoUrl} ")
        if (patient.photoUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(patient.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Patient photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )


        } else {
            Text(
                text = "AA",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

        }
    }
}