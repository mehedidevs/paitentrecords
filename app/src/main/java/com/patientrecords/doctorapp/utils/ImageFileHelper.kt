package com.patientrecords.doctorapp.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Helper class for image file operations
 */
object ImageFileHelper {

    /**
     * Create a temporary file for camera capture
     */
    fun createTempImageFile(context: Context): File {
        val fileName = "patient_photo_${System.currentTimeMillis()}"
        return File.createTempFile(fileName, ".jpg", context.cacheDir)
    }

    /**
     * Copy URI content to a temporary file
     */
    suspend fun copyUriToFile(context: Context, uri: Uri): File? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    context.contentResolver.openInputStream(uri) ?: return@withContext null
                val tempFile = createTempImageFile(context)

                FileOutputStream(tempFile).use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()

                tempFile
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Get image bytes from URI
     */
    suspend fun getImageBytes(context: Context, uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    context.contentResolver.openInputStream(uri) ?: return@withContext null
                val bytes = inputStream.readBytes()
                inputStream.close()
                bytes
            } catch (e: Exception) {
                null
            }
        }
    }
}