
package com.moneynest.app.feature.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember {
        mutableStateOf("Makanan & Minuman")
    }
    var type by remember { mutableStateOf("EXPENSE") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val saveState by viewModel.saveState.collectAsState()

    val isSaving = saveState is SaveState.Loading
    val isSaved = saveState is SaveState.Success

    val categories = listOf(
        "Makanan & Minuman",
        "Transportasi",
        "Belanja",
        "Gaji",
        "Lainnya"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Add Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Record your financial activity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // TRANSACTION FORM
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Transaction Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // EXPENSE
                    val expenseSelected = type == "EXPENSE"

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !isSaving && !isSaved) {
                                type = "EXPENSE"
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (expenseSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = if (expenseSelected) {
                            androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary
                            )
                        } else {
                            null
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (expenseSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )

                            Text(
                                text = "Expense",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (expenseSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    // INCOME
                    val incomeSelected = type == "INCOME"

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !isSaving && !isSaved) {
                                type = "INCOME"
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (incomeSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = if (incomeSelected) {
                            androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary
                            )
                        } else {
                            null
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (incomeSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )

                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (incomeSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(
                        alpha = 0.3f
                    )
                )

                // TITLE
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = ""
                    },
                    label = { Text("Transaction Name") },
                    placeholder = { Text("Contoh: Belanja Bulanan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving && !isSaved,
                    shape = RoundedCornerShape(14.dp)
                )

                // AMOUNT
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newAmount ->
                        if (newAmount.all { it.isDigit() }) {
                            amount = newAmount
                            errorMessage = ""
                        }
                    },
                    label = { Text("Amount (Rp)") },
                    placeholder = { Text("Contoh: 250000") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving && !isSaved,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                // CATEGORY
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        enabled = !isSaving && !isSaved,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = category,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    category = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // ERROR VALIDATION
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // SAVE BUTTON
        Button(
            onClick = {
                val amountValue = amount.toLongOrNull()

                errorMessage = when {
                    title.isBlank() ->
                        "Nama transaksi tidak boleh kosong"

                    amountValue == null ->
                        "Jumlah uang harus berupa angka"

                    amountValue <= 0L ->
                        "Jumlah uang harus lebih dari 0"

                    else -> ""
                }

                if (errorMessage.isEmpty() && amountValue != null) {
                    viewModel.saveTransaction(
                        title = title.trim(),
                        category = category,
                        amount = amountValue,
                        type = type
                    )
                }
            },
            enabled = !isSaving && !isSaved,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(16.dp)
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
                    else -> "Save Transaction"
                },
                fontWeight = FontWeight.SemiBold
            )
        }

        // SAVE RESULT
        when (val state = saveState) {
            is SaveState.Success -> {
                Text(
                    text = "Transaksi berhasil disimpan!",
                    color = Color(0xFF16885B),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is SaveState.Error -> {
                Text(
                    text = "Gagal menyimpan: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> Unit
        }
    }
}
