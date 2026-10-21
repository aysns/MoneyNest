
package com.moneynest.app.feature.scanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moneynest.app.data.local.entity.TransactionItemEntity
import com.moneynest.app.feature.scanner.model.ReceiptData
import java.util.Locale

@Composable
fun ReceiptReviewScreen(
    receiptData: ReceiptData,
    viewModel: ReceiptSaveViewModel = hiltViewModel()
) {
    val saveState by viewModel.saveState.collectAsState()

    val isSaving = saveState is ReceiptSaveState.Loading
    val isSaved = saveState is ReceiptSaveState.Success

    var storeName by remember(receiptData) {
        mutableStateOf(receiptData.storeName)
    }

    var transactionDate by remember(receiptData) {
        mutableStateOf(receiptData.date)
    }

    val itemNames = remember(receiptData) {
        receiptData.items.map {
            mutableStateOf(it.name)
        }
    }

    val itemQuantities = remember(receiptData) {
        receiptData.items.map {
            mutableStateOf(it.quantity.toString())
        }
    }

    val itemPrices = remember(receiptData) {
        receiptData.items.map {
            mutableStateOf(it.unitPrice.toString())
        }
    }

    // Menggabungkan diskon lama dan diskon hasil OCR.
    // Saat ini parser belum otomatis mengisi totalDiscount.
    val initialDiscount = remember(receiptData) {
        receiptData.totalDiscount +
                receiptData.items.sumOf { it.discount }
    }

    var totalDiscountInput by remember(receiptData) {
        mutableStateOf(initialDiscount.toString())
    }

    fun formatRupiah(amount: Long): String {
        return "Rp %,d".format(Locale.US, amount)
            .replace(',', '.')
    }

    val totalBeforeDiscount = receiptData.items.indices.sumOf { index ->
        val quantity =
            itemQuantities[index].value.toLongOrNull() ?: 0L

        val price =
            itemPrices[index].value.toLongOrNull() ?: 0L

        quantity * price
    }

    val totalDiscount = totalDiscountInput.toLongOrNull()

    val calculatedTotal = (
            totalBeforeDiscount - (totalDiscount ?: 0L)
            ).coerceAtLeast(0L)

    val isDiscountValid =
        totalDiscount != null &&
                totalDiscount >= 0L &&
                totalDiscount <= totalBeforeDiscount

    val isReceiptValid =
        storeName.isNotBlank() &&
                transactionDate.isNotBlank() &&
                receiptData.items.isNotEmpty() &&
                isDiscountValid &&
                receiptData.items.indices.all { index ->
                    val name = itemNames[index].value
                    val quantity =
                        itemQuantities[index].value.toIntOrNull()
                    val price =
                        itemPrices[index].value.toLongOrNull()

                    name.isNotBlank() &&
                            quantity != null &&
                            quantity > 0 &&
                            price != null &&
                            price > 0L
                }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Column {
                Text(
                    text = "Receipt Review",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Review your scanned transaction",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // STORE INFORMATION
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
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Store Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Store Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving && !isSaved,
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = transactionDate,
                    onValueChange = { transactionDate = it },
                    label = { Text("Transaction Date") },
                    placeholder = { Text("DD-MM-YYYY") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving && !isSaved,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        // ITEMS HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Purchased Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "${receiptData.items.size} items",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        if (receiptData.items.isEmpty()) {
            Text(
                text = "Belum ada barang yang terdeteksi.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            receiptData.items.forEachIndexed { index, _ ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Item ${index + 1}",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedTextField(
                            value = itemNames[index].value,
                            onValueChange = {
                                itemNames[index].value = it
                            },
                            label = { Text("Nama Barang") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isSaving && !isSaved,
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = itemQuantities[index].value,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) {
                                    itemQuantities[index].value = value
                                }
                            },
                            label = { Text("Jumlah Barang") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isSaving && !isSaved,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = itemPrices[index].value,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) {
                                    itemPrices[index].value = value
                                }
                            },
                            label = { Text("Harga Satuan (Rp)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isSaving && !isSaved,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        val quantity =
                            itemQuantities[index].value
                                .toLongOrNull() ?: 0L

                        val price =
                            itemPrices[index].value
                                .toLongOrNull() ?: 0L

                        val subtotal = quantity * price

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Subtotal",
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = formatRupiah(subtotal),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // DISCOUNT SUMMARY
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Ringkasan Belanja",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Harga Barang")

                    Text(
                        text = formatRupiah(totalBeforeDiscount),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedTextField(
                    value = totalDiscountInput,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            totalDiscountInput = value
                        }
                    },
                    label = { Text("Total Diskon Gabungan (Rp)") },
                    supportingText = {
                        Text(
                            "Gabungan seluruh diskon. " +
                                    "Periksa dan edit sesuai struk."
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving && !isSaved,
                    isError = !isDiscountValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                if (!isDiscountValid) {
                    Text(
                        text = "Diskon harus antara Rp 0 dan " +
                                formatRupiah(totalBeforeDiscount),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Bayar",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formatRupiah(calculatedTotal),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // TOTAL CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Total Amount",
                    color = MaterialTheme.colorScheme.onPrimary
                        .copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = formatRupiah(calculatedTotal),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // CEK TOTAL PADA STRUK
        if (
            receiptData.totalAmount != null &&
            receiptData.totalAmount != calculatedTotal
        ) {
            Text(
                text = "Total pada struk: " +
                        formatRupiah(receiptData.totalAmount) +
                        ". Periksa kembali harga barang dan diskon.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (!isReceiptValid) {
            Text(
                text = "Periksa kembali nama toko, tanggal, " +
                        "barang, jumlah, harga, dan diskon.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        // SAVE BUTTON
        Button(
            onClick = {
                if (isReceiptValid && !isSaving && !isSaved) {

                    val editedItems =
                        receiptData.items.mapIndexed { index, _ ->
                            TransactionItemEntity(
                                transactionId = 0L,
                                name = itemNames[index].value.trim(),
                                quantity = itemQuantities[index].value
                                    .toInt(),
                                unitPrice = itemPrices[index].value
                                    .toLong(),
                                discount = 0L
                            )
                        }

                    viewModel.saveReceipt(
                        storeName = storeName,
                        transactionDate = transactionDate,
                        totalAmount = calculatedTotal,
                        items = editedItems,
                        totalDiscount = totalDiscount ?: 0L
                    )
                }
            },
            enabled = isReceiptValid && !isSaving && !isSaved,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(14.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    imageVector = if (isSaved) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Save
                    },
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = when {
                    isSaving -> "Menyimpan..."
                    isSaved -> "Berhasil Disimpan"
                    else -> "Simpan Hasil Scan"
                },
                fontWeight = FontWeight.SemiBold
            )
        }

        when (val state = saveState) {
            is ReceiptSaveState.Success -> {
                Text(
                    text = "Struk dan daftar barang berhasil disimpan!",
                    color = Color(0xFF16885B),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is ReceiptSaveState.Error -> {
                Text(
                    text = "Gagal menyimpan: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )

                TextButton(
                    onClick = {
                        viewModel.resetSaveState()
                    }
                ) {
                    Text("Coba Lagi")
                }
            }

            else -> Unit
        }
    }
}
