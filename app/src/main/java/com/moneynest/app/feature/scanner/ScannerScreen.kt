
package com.moneynest.app.feature.scanner

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ScannerScreen() {

    val context = LocalContext.current

    var selectedImageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    var cameraImageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    var isOcrLoading by remember {
        mutableStateOf(false)
    }

    var ocrResult by remember {
        mutableStateOf("")
    }

    var ocrError by remember {
        mutableStateOf<String?>(null)
    }

    var hasScanned by remember {
        mutableStateOf(false)
    }

    val ocrAnalyzer = remember {
        ReceiptOcrAnalyzer()
    }

    val receiptParser = remember {
        ReceiptParser()
    }

    val layoutAnalyzer = remember {
        ReceiptLayoutAnalyzer()
    }

    val parsedReceipt = remember(ocrResult) {
        if (ocrResult.isNotBlank()) {
            receiptParser.parse(ocrResult)
        } else {
            null
        }
    }

    DisposableEffect(ocrAnalyzer) {
        onDispose {
            ocrAnalyzer.close()
        }
    }

    fun resetScan() {
        hasScanned = false
        ocrResult = ""
        ocrError = null
    }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                resetScan()
                selectedImageUri = uri
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                resetScan()
                selectedImageUri = cameraImageUri
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Receipt Scanner",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Scan your receipt, track your spending",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // CAMERA & GALLERY CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Text(
                    text = "Scan Your Receipt",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Take a photo or select a receipt from your gallery.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        try {
                            val uri = createReceiptImageUri(context)
                            cameraImageUri = uri
                            cameraLauncher.launch(uri)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Tidak dapat membuka kamera",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !isOcrLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = "Take Photo",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts
                                    .PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    enabled = !isOcrLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = "Choose from Gallery",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // PREVIEW & OCR
        selectedImageUri?.let { uri ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Receipt Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    AsyncImage(
                        model = uri,
                        contentDescription = "Preview foto struk",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Foto berhasil dipilih",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = {
                            isOcrLoading = true
                            resetScan()

                            ocrAnalyzer.recognizeTextWithPosition(
                                context = context,
                                imageUri = uri,
                                onSuccess = { elements ->
                                    val reconstructedLines =
                                        layoutAnalyzer.groupIntoLines(elements)

                                    ocrResult =
                                        reconstructedLines.joinToString("\n")

                                    hasScanned = true
                                    isOcrLoading = false
                                },
                                onError = { exception ->
                                    ocrError = exception.message
                                        ?: "Gagal membaca teks struk"

                                    isOcrLoading = false
                                }
                            )
                        },
                        enabled = !isOcrLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null
                        )

                        Spacer(Modifier.width(8.dp))

                        Text("Baca Teks Struk")
                    }

                    if (isOcrLoading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            selectedImageUri = null
                            resetScan()
                        },
                        enabled = !isOcrLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null
                        )

                        Spacer(Modifier.width(8.dp))

                        Text("Ganti Foto")
                    }
                }
            }

            // OCR RESULT
            if (ocrResult.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Hasil Scan Struk",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        HorizontalDivider()

                        SelectionContainer {
                            Text(
                                text = ocrResult,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // RECEIPT REVIEW
            parsedReceipt?.let { receipt ->
                ReceiptReviewScreen(
                    receiptData = receipt
                )
            }

            // ERROR MESSAGE
            ocrError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (hasScanned && ocrResult.isBlank()) {
                Text(
                    text = "Tidak ada teks yang terdeteksi. Coba gunakan foto struk yang lebih jelas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun createReceiptImageUri(
    context: Context
): Uri {
    val directory = File(
        context.cacheDir,
        "receipts"
    )

    directory.mkdirs()

    val file = File.createTempFile(
        "receipt_",
        ".jpg",
        directory
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
