
package com.moneynest.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneynest.app.data.local.entity.TransactionEntity
import com.moneynest.app.data.local.entity.TransactionItemEntity
import com.moneynest.app.data.repository.TransactionRepository
import com.moneynest.app.data.repository.ReceiptTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TransactionActionState {
    data object Idle : TransactionActionState()
    data object Loading : TransactionActionState()
    data object Success : TransactionActionState()
    data class Error(val message: String) : TransactionActionState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val receiptRepository: ReceiptTransactionRepository
) : ViewModel() {

    // Semua transaksi dari Room
    val transactions: StateFlow<List<TransactionEntity>> =
        repository.getAllTransaction()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Detail barang dari hasil scan struk
    private val _receiptItems =
        MutableStateFlow<List<TransactionItemEntity>>(emptyList())

    val receiptItems = _receiptItems.asStateFlow()

    private val _isLoadingReceiptItems =
        MutableStateFlow(false)

    val isLoadingReceiptItems =
        _isLoadingReceiptItems.asStateFlow()

    // Status edit dan delete
    private val _actionState =
        MutableStateFlow<TransactionActionState>(
            TransactionActionState.Idle
        )

    val actionState = _actionState.asStateFlow()

    // Mengambil daftar barang
    fun loadReceiptItems(transactionId: Long) {
        viewModelScope.launch {
            _isLoadingReceiptItems.value = true
            _receiptItems.value = emptyList()

            try {
                _receiptItems.value =
                    receiptRepository.getReceiptItems(transactionId)
            } catch (e: Exception) {
                _receiptItems.value = emptyList()
            } finally {
                _isLoadingReceiptItems.value = false
            }
        }
    }

    // Memperbarui transaksi
    fun updateTransaction(transaction: TransactionEntity) {
        if (_actionState.value is TransactionActionState.Loading) {
            return
        }

        viewModelScope.launch {
            _actionState.value = TransactionActionState.Loading

            try {
                repository.updateTransaction(transaction)

                _actionState.value =
                    TransactionActionState.Success

            } catch (e: Exception) {
                _actionState.value =
                    TransactionActionState.Error(
                        e.message ?: "Gagal memperbarui transaksi"
                    )
            }
        }
    }

    // Menghapus transaksi
    fun deleteTransaction(transaction: TransactionEntity) {
        if (_actionState.value is TransactionActionState.Loading) {
            return
        }

        viewModelScope.launch {
            _actionState.value = TransactionActionState.Loading

            try {
                repository.deleteTransaction(transaction)

                _actionState.value =
                    TransactionActionState.Success

            } catch (e: Exception) {
                _actionState.value =
                    TransactionActionState.Error(
                        e.message ?: "Gagal menghapus transaksi"
                    )
            }
        }
    }

    // Mengembalikan status ke awal
    fun resetActionState() {
        _actionState.value = TransactionActionState.Idle
    }
}
