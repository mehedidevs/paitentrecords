/*
package com.patientrecords.doctorapp.patientdetils.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.patientrecords.doctorapp.patientdetils.data.Prescription
import com.patientrecords.doctorapp.patientdetils.data.Visit
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Patient
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// ==================== Prescription PDF Generator ====================

object PrescriptionGenerator {

    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 40f

    */
/**
     * Generate a prescription PDF for a visit
     *//*

    fun generatePrescriptionPdf(
        context: Context,
        patient: Patient,
        visit: Visit,
        prescriptions: List<Prescription>,
        doctorName: String = "Dr. Homeopathy Clinic"
    ): File? {
        val pdfDocument = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            var yPosition = MARGIN

            // Title Paint
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
                color = Color.parseColor("#2BEE5B")
            }

            // Header Paint
            val headerPaint = Paint().apply {
                textSize = 14f
                isFakeBoldText = true
                color = Color.BLACK
            }

            // Body Paint
            val bodyPaint = Paint().apply {
                textSize = 12f
                color = Color.DKGRAY
            }

            // Small Paint
            val smallPaint = Paint().apply {
                textSize = 10f
                color = Color.GRAY
            }

            // ==================== Header ====================
            canvas.drawText(doctorName, MARGIN, yPosition + 20, titlePaint)
            yPosition += 40

            canvas.drawText("Homeopathy & Wellness Center", MARGIN, yPosition, smallPaint)
            yPosition += 15

            canvas.drawText("Contact: +91 9999999999", MARGIN, yPosition, smallPaint)
            yPosition += 30

            // Divider
            canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, bodyPaint)
            yPosition += 20

            // ==================== Patient Info ====================
            canvas.drawText("PATIENT INFORMATION", MARGIN, yPosition, headerPaint)
            yPosition += 20

            canvas.drawText("Name: ${patient.fullName}", MARGIN, yPosition, bodyPaint)
            canvas.drawText("Age: ${patient.age} Years", PAGE_WIDTH / 2f, yPosition, bodyPaint)
            yPosition += 18

            canvas.drawText(
                "Gender: ${
                    patient.gender.name.lowercase().replaceFirstChar { it.uppercase() }
                }", MARGIN, yPosition, bodyPaint
            )
            canvas.drawText("Phone: ${patient.mobileNumber}", PAGE_WIDTH / 2f, yPosition, bodyPaint)
            yPosition += 18

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            canvas.drawText("Date: $currentDate", MARGIN, yPosition, bodyPaint)
            yPosition += 30

            // Divider
            canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, bodyPaint)
            yPosition += 20

            // ==================== Chief Complaints ====================
            if (visit.symptoms.isNotBlank()) {
                canvas.drawText("CHIEF COMPLAINTS", MARGIN, yPosition, headerPaint)
                yPosition += 18

                // Word wrap for long symptoms
                val words = visit.symptoms.split(" ")
                var line = ""
                val maxWidth = PAGE_WIDTH - 2 * MARGIN

                for (word in words) {
                    val testLine = if (line.isEmpty()) word else "$line $word"
                    val textWidth = bodyPaint.measureText(testLine)

                    if (textWidth > maxWidth) {
                        canvas.drawText(line, MARGIN, yPosition, bodyPaint)
                        yPosition += 16
                        line = word
                    } else {
                        line = testLine
                    }
                }

                if (line.isNotEmpty()) {
                    canvas.drawText(line, MARGIN, yPosition, bodyPaint)
                    yPosition += 25
                }
            }

            // ==================== Diagnosis ====================
            if (!visit.diagnosis.isNullOrBlank()) {
                canvas.drawText("DIAGNOSIS", MARGIN, yPosition, headerPaint)
                yPosition += 18
                canvas.drawText(visit.diagnosis, MARGIN, yPosition, bodyPaint)
                yPosition += 25
            }

            // ==================== Prescriptions ====================
            canvas.drawText("PRESCRIPTION", MARGIN, yPosition, headerPaint)
            yPosition += 25

            // Table Header
            val colMedicine = MARGIN
            val colPotency = 180f
            val colDosage = 260f
            val colFrequency = 340f
            val colDays = 450f

            canvas.drawText("Medicine", colMedicine, yPosition, Paint().apply {
                textSize = 11f
                isFakeBoldText = true
            })
            canvas.drawText("Potency", colPotency, yPosition, Paint().apply {
                textSize = 11f
                isFakeBoldText = true
            })
            canvas.drawText("Dosage", colDosage, yPosition, Paint().apply {
                textSize = 11f
                isFakeBoldText = true
            })
            canvas.drawText("Frequency", colFrequency, yPosition, Paint().apply {
                textSize = 11f
                isFakeBoldText = true
            })
            canvas.drawText("Days", colDays, yPosition, Paint().apply {
                textSize = 11f
                isFakeBoldText = true
            })
            yPosition += 20

            // Table rows
            prescriptions.forEachIndexed { index, prescription ->
                canvas.drawText(
                    "${index + 1}. ${prescription.medicineName}",
                    colMedicine,
                    yPosition,
                    bodyPaint
                )
                canvas.drawTextprescription.potency, colPotency, yPosition, bodyPaint)
                canvas.drawText(prescription.dosage, colDosage, yPosition, bodyPaint)
                canvas.drawText(prescription.frequency, colFrequency, yPosition, bodyPaint)
                canvas.drawText("${prescription.durationDays}", colDays, yPosition, bodyPaint)
                yPosition += 20
            }

            yPosition += 20

            // ==================== Instructions ====================
            canvas.drawText("INSTRUCTIONS", MARGIN, yPosition, headerPaint)
            yPosition += 18

            val instructions = listOf(
                "• Take medicines as prescribed",
                "• Avoid coffee, onion, garlic during medication",
                "• Keep medicines away from strong odors",
                "• Store in cool, dry place",
                "• Follow up as advised"
            )

            instructions.forEach { instruction ->
                canvas.drawText(instruction, MARGIN, yPosition, smallPaint)
                yPosition += 14
            }

            yPosition += 30

            // ==================== Signature ====================
            canvas.drawLine(
                (PAGE_WIDTH - 180).toFloat(),
                yPosition,
                PAGE_WIDTH - MARGIN,
                yPosition,
                bodyPaint
            )
            yPosition += 15
            canvas.drawText(
                "Doctor's Signature",
                (PAGE_WIDTH - 160).toFloat(), yPosition, smallPaint
            )

            // Footer
            canvas.drawText(
                "This is a computer generated prescription",
                (PAGE_WIDTH / 2 - 100).toFloat(),
                PAGE_HEIGHT - 30f,
                smallPaint
            )

            pdfDocument.finishPage(page)

            // Save file
            val fileName =
                "Prescription_${
                    patient.fullName.replace(
                        " ",
                        "_"
                    )
                }_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            FileOutputStream(file).use { output ->
                pdfDocument.writeTo(output)
            }

            return file

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            pdfDocument.close()
        }
    }

    */
/**
     * Generate bill summary PDF
     *//*

    fun generateBillPdf(
        context: Context,
        patient: Patient,
        prescriptions: List<Prescription>,
        medicineTotal: Double,
        consultationFee: Double,
        discount: Double,
        grandTotal: Double
    ): File? {
        val pdfDocument = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            var yPosition = MARGIN

            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }

            val headerPaint = Paint().apply {
                textSize = 14f
                isFakeBoldText = true
            }

            val bodyPaint = Paint().apply {
                textSize = 12f
                color = Color.DKGRAY
            }

            // Title
            canvas.drawText("BILL SUMMARY", MARGIN, yPosition + 20, titlePaint)
            yPosition += 50

            // Patient Info
            canvas.drawText("Patient: ${patient.fullName}", MARGIN, yPosition, headerPaint)
            yPosition += 20
            canvas.drawText("ID: #${patient.id.take(8)}", MARGIN, yPosition, bodyPaint)
            yPosition += 40

            // Items
            canvas.drawText("MEDICINE DETAILS", MARGIN, yPosition, headerPaint)
            yPosition += 25

            prescriptions.forEach { prescription ->
                canvas.drawText(
                    "${prescription.medicineName} (${prescription.potency})",
                    MARGIN,
                    yPosition,
                    bodyPaint
                )
                canvas.drawText(
                    "₹${prescription.price}",
                    (PAGE_WIDTH - 100).toFloat(),
                    yPosition,
                    bodyPaint
                )
                yPosition += 18
            }

            yPosition += 20
            canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, bodyPaint)
            yPosition += 25

            // Totals
            canvas.drawText("Medicine Total:", MARGIN, yPosition, bodyPaint)
            canvas.drawText(
                "₹${"%.2f".format(medicineTotal)}",
                (PAGE_WIDTH - 100).toFloat(),
                yPosition,
                bodyPaint
            )
            yPosition += 20

            canvas.drawText("Consultation Fee:", MARGIN, yPosition, bodyPaint)
            canvas.drawText(
                "₹${"%.2f".format(consultationFee)}",
                (PAGE_WIDTH - 100).toFloat(),
                yPosition,
                bodyPaint
            )
            yPosition += 20

            if (discount > 0) {
                canvas.drawText("Discount:", MARGIN, yPosition, bodyPaint)
                canvas.drawText(
                    "-₹${"%.2f".format(discount)}",
                    (PAGE_WIDTH - 100).toFloat(),
                    yPosition,
                    Paint().apply {
                        textSize = 12f
                        color = Color.parseColor("#2BEE5B")
                    })
                yPosition += 20
            }

            yPosition += 10
            canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, bodyPaint)
            yPosition += 25

            val grandTotalPaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
            }
            canvas.drawText("GRAND TOTAL:", MARGIN, yPosition, grandTotalPaint)
            canvas.drawText(
                "₹${"%.2f".format(grandTotal)}",
                (PAGE_WIDTH - 110).toFloat(),
                yPosition,
                grandTotalPaint
            )

            pdfDocument.finishPage(page)

            val fileName =
                "Bill_${patient.fullName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            FileOutputStream(file).use { output ->
                pdfDocument.writeTo(output)
            }

            return file

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            pdfDocument.close()
        }
    }
}

// ==================== Sharing Utilities ====================

object SharingUtils {

    */
/**
     * Share prescription via WhatsApp
     *//*

    fun shareViaWhatsApp(
        context: Context,
        patient: Patient,
        prescriptions: List<Prescription>,
        visit: Visit,
        phoneNumber: String? = null
    ) {
        val message = buildPrescriptionMessage(patient, prescriptions, visit)

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, message)
            }

            // If phone number provided, open chat directly
            if (!phoneNumber.isNullOrBlank()) {
                val formattedNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
                val uri = Uri.parse("https://wa.me/$formattedNumber?text=${Uri.encode(message)}")
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            } else {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            // WhatsApp not installed, use general share
            shareGeneral(context, message)
        }
    }

    */
/**
     * Share PDF file via WhatsApp
     *//*

    fun sharePdfViaWhatsApp(
        context: Context,
        pdfFile: File,
        phoneNumber: String? = null
    ) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }

    */
/**
     * General share functionality
     *//*

    fun shareGeneral(context: Context, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, "Share Prescription"))
    }

    */
/**
     * Build prescription text message
     *//*

    private fun buildPrescriptionMessage(
        patient: Patient,
        prescriptions: List<Prescription>,
        visit: Visit
    ): String {
        return buildString {
            appendLine("*PRESCRIPTION*")
            appendLine("━━━━━━━━━━━━━━━━━")
            appendLine()
            appendLine("*Patient:* ${patient.fullName}")
            appendLine("*Age:* ${patient.age} Years")
            appendLine(
                "*Date:* ${
                    SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    ).format(Date())
                }"
            )
            appendLine()

            if (visit.symptoms.isNotBlank()) {
                appendLine("*Symptoms:*")
                appendLine(visit.symptoms)
                appendLine()
            }

            if (!visit.diagnosis.isNullOrBlank()) {
                appendLine("*Diagnosis:* ${visit.diagnosis}")
                appendLine()
            }

            appendLine("*Medicines:*")
            appendLine("━━━━━━━━━━━━━━━━━")

            prescriptions.forEachIndexed { index, prescription ->
                appendLine()
                appendLine("${index + 1}. *${prescription.medicineName}* ${prescription.potency}")
                appendLine("   Dosage: ${prescription.dosage}")
                appendLine("   Frequency: ${prescription.frequency}")
                appendLine("   Duration: ${prescription.durationDays} days")
            }

            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━")
            appendLine("*Instructions:*")
            appendLine("• Take medicines as prescribed")
            appendLine("• Avoid coffee & onion")
            appendLine("• Follow up as advised")
            appendLine()
            appendLine("_This is an electronically generated prescription_")
        }
    }
}

// ==================== Print Utilities ====================

object PrintUtils {

    */
/**
     * Print PDF document
     *//*

    fun printPdf(context: Context, pdfFile: File, jobName: String = "Prescription") {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val printAdapter = PdfDocumentAdapter(context, uri, pdfFile.name)

            printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("default", "default", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Print error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

// PDF Print Adapter
class PdfDocumentAdapter(
    private val context: Context,
    private val pdfUri: Uri,
    private val fileName: String
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder(fileName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        try {
            context.contentResolver.openInputStream(pdfUri)?.use { input ->
                ParcelFileDescriptor.AutoCloseOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        }
    }
}
*/
