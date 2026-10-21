
package com.moneynest.app.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moneynest.app.data.local.entity.TransactionEntity

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val receiptItems by viewModel.receiptItems.collectAsState()
    val isLoadingReceiptItems by viewModel.isLoadingReceiptItems.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var selectedTransaction by remember {
        mutableStateOf<TransactionEntity?>(null)
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var editTitle by remember { mutableStateOf("") }
    var editAmount by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }
    var editType by remember { mutableStateOf("EXPENSE") }
    var editError by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val isLoading = actionState is TransactionActionState.Loading

    val categories = listOf(
        "Makanan & Minuman",
        "Transportasi",
        "Belanja",
        "Gaji",
        "Lainnya"
    )

    val currentTransaction = selectedTransaction?.let { selected ->
        transactions.find { it.id == selected.id }
    }

    LaunchedEffect(actionState) {
        if (actionState is TransactionActionState.Success) {
            showEditDialog = false
            showDeleteDialog = false
            selectedTransaction = null
            viewModel.resetActionState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Transaction History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Track your financial activity",
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
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // TRANSACTION COUNT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Transactions",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${transactions.size} records",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )

                    Text("Belum ada transaksi")

                    Text(
                        text = "Transaksi yang kamu simpan akan muncul di sini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = transactions,
                    key = { it.id }
                ) { transaction ->

                    val isIncome = transaction.type == "INCOME"

                    val amountColor = if (isIncome) {
                        Color(0xFF16885B)
                    } else {
                        Color(0xFFE34B68)
                    }

                    Card(
                        onClick = {
                            viewModel.resetActionState()
                            selectedTransaction = transaction
                            viewModel.loadReceiptItems(transaction.id)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        if (isIncome) {
                                            Color(0xFFE6F8EE)
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer
                                        },
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isIncome) {
                                        Icons.Default.AccountBalanceWallet
                                    } else {
                                        Icons.Default.ShoppingBag
                                    },
                                    contentDescription = null,
                                    tint = if (isIncome) {
                                        Color(0xFF16885B)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = transaction.title,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "${transaction.category} · " +
                                            if (isIncome) "Income" else "Expense",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Text(
                                text = "${if (isIncome) "+" else "-"}Rp %,d"
                                    .format(transaction.amount)
                                    .replace(',', '.'),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = amountColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    // DETAIL TRANSACTION
    if (
        selectedTransaction != null &&
        !showEditDialog &&
        !showDeleteDialog
    ) {
        val transaction = currentTransaction ?: selectedTransaction!!

        AlertDialog(
            onDismissRequest = {
                selectedTransaction = null
            },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Detail Transaksi",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Kategori: ${transaction.category}"
                    )

                    Text(
                        text = "Jenis: ${
                            if (transaction.type == "INCOME") {
                                "Pemasukan"
                            } else {
                                "Pengeluaran"
                            }
                        }"
                    )

                    Text(
                        text = "Jumlah: Rp %,d"
                            .format(transaction.amount)
                            .replace(',', '.'),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (transaction.transactionDate.isNotBlank()) {
                        Text(
                            text = "Tanggal: ${transaction.transactionDate}"
                        )
                    }

                    // RINGKASAN DISKON GABUNGAN
                    // amount adalah total bayar setelah diskon.
                    // Harga awal = total bayar + diskon gabungan.
                    if (transaction.totalDiscount > 0L) {
                        HorizontalDivider()

                        Text(
                            text = "Ringkasan Belanja",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Harga Barang")
                            Text(
                                text = "Rp %,d".format(
                                    transaction.amount + transaction.totalDiscount
                                ).replace(',', '.'),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Diskon")
                            Text(
                                text = "- Rp %,d".format(transaction.totalDiscount)
                                    .replace(',', '.'),
                                color = Color(0xFF16885B),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Bayar", fontWeight = FontWeight.Bold)
                            Text(
                                text = "Rp %,d".format(transaction.amount)
                                    .replace(',', '.'),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    HorizontalDivider()

                    when {
                        isLoadingReceiptItems -> {
                            CircularProgressIndicator()
                        }

                        receiptItems.isEmpty() -> {
                            Text(
                                text = "Tidak ada detail barang untuk transaksi ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        else -> {
                            Text(
                                text = "Daftar Barang",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            LazyColumn(
                                modifier = Modifier.heightIn(max = 220.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(receiptItems) { item ->
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Text(
                                            text = "${item.quantity} x Rp %,d"
                                                .format(item.unitPrice)
                                                .replace(',', '.'),
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        Text(
                                            text = "Subtotal: Rp %,d"
                                                .format(
                                                    (
                                                            item.quantity.toLong() *
                                                                    item.unitPrice -
                                                                    item.discount
                                                            ).coerceAtLeast(0L)
                                                )
                                                .replace(',', '.'),
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editTitle = transaction.title
                        editAmount = transaction.amount.toString()
                        editCategory = transaction.category
                        editType = transaction.type
                        editError = ""
                        viewModel.resetActionState()
                        showEditDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Edit")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            viewModel.resetActionState()
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    TextButton(
                        onClick = {
                            selectedTransaction = null
                        }
                    ) {
                        Text("Tutup")
                    }
                }
            }
        )
    }

    // EDIT DIALOG
    if (showEditDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showEditDialog = false
                    viewModel.resetActionState()
                }
            },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Edit Transaction",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Nama Transaksi") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { newAmount ->
                            if (newAmount.all { it.isDigit() }) {
                                editAmount = newAmount
                            }
                        },
                        label = { Text("Jumlah (Rp)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Box {
                        OutlinedButton(
                            onClick = { categoryExpanded = true },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = editCategory,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = {
                                categoryExpanded = false
                            }
                        ) {
                            (
                                    categories +
                                            listOf(editCategory)
                                    ).distinct().forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category) },
                                        onClick = {
                                            editCategory = category
                                            categoryExpanded = false
                                        }
                                    )
                                }
                        }
                    }

                    Text(
                        text = "Jenis Transaksi",
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = editType == "EXPENSE",
                            onClick = { editType = "EXPENSE" },
                            enabled = !isLoading
                        )
                        Text("Expense")

                        Spacer(Modifier.width(12.dp))

                        RadioButton(
                            selected = editType == "INCOME",
                            onClick = { editType = "INCOME" },
                            enabled = !isLoading
                        )
                        Text("Income")
                    }

                    if (editError.isNotBlank()) {
                        Text(
                            text = editError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (actionState is TransactionActionState.Error) {
                        Text(
                            text = (actionState as TransactionActionState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountValue = editAmount.toLongOrNull()

                        editError = when {
                            editTitle.isBlank() ->
                                "Nama transaksi tidak boleh kosong"

                            amountValue == null ->
                                "Jumlah harus berupa angka"

                            amountValue <= 0L ->
                                "Jumlah harus lebih dari 0"

                            else -> ""
                        }

                        if (editError.isEmpty() && amountValue != null) {
                            selectedTransaction?.let { transaction ->
                                viewModel.updateTransaction(
                                    transaction.copy(
                                        title = editTitle.trim(),
                                        amount = amountValue,
                                        category = editCategory,
                                        type = editType
                                    )
                                )
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Menyimpan..." else "Simpan")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        viewModel.resetActionState()
                    },
                    enabled = !isLoading
                ) {
                    Text("Batal")
                }
            }
        )
    }

    // DELETE CONFIRMATION
    if (showDeleteDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showDeleteDialog = false
                    viewModel.resetActionState()
                }
            },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Hapus Transaksi?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Transaksi \"${selectedTransaction?.title}\" akan dihapus secara permanen."
                    )

                    Text(
                        text = "Tindakan ini tidak dapat dibatalkan.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (actionState is TransactionActionState.Error) {
                        Text(
                            text = (actionState as TransactionActionState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedTransaction?.let {
                            viewModel.deleteTransaction(it)
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(if (isLoading) "Menghapus..." else "Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.resetActionState()
                    },
                    enabled = !isLoading
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

