
package com.moneynest.app.feature.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneynest.app.data.local.entity.TransactionEntity
import com.moneynest.app.data.local.entity.TransactionItemEntity
import com.moneynest.app.data.repository.ReceiptTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReceiptSaveState {
    data object Idle : ReceiptSaveState
    data object Loading : ReceiptSaveState
    data object Success : ReceiptSaveState
    data class Error(val message: String) : ReceiptSaveState
}

@HiltViewModel
class ReceiptSaveViewModel @Inject constructor(
    private val repository: ReceiptTransactionRepository
) : ViewModel() {

    private val _saveState =
        MutableStateFlow<ReceiptSaveState>(ReceiptSaveState.Idle)

    val saveState = _saveState.asStateFlow()

    fun saveReceipt(
        storeName: String,
        transactionDate: String,
        totalAmount: Long,
        items: List<TransactionItemEntity>,
        totalDiscount: Long = 0L
    ) {
        if (_saveState.value != ReceiptSaveState.Idle) return

        viewModelScope.launch {
            _saveState.value = ReceiptSaveState.Loading

            try {
                require(totalAmount >= 0L) {
                    "Total pembayaran tidak valid"
                }

                require(totalDiscount >= 0L) {
                    "Total diskon tidak boleh negatif"
                }

                val transaction = TransactionEntity(
                    title = "Belanja di ${storeName.trim()}",
                    category = "Belanja",
                    amount = totalAmount,
                    type = "EXPENSE",
                    transactionDate = transactionDate.trim(),
                    totalDiscount = totalDiscount
                )

                repository.saveReceipt(
                    transaction = transaction,
                    items = items
                )

                _saveState.value = ReceiptSaveState.Success

            } catch (e: Exception) {
                _saveState.value = ReceiptSaveState.Error(
                    e.message ?: "Gagal menyimpan struk"
                )
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = ReceiptSaveState.Idle
    }
}
