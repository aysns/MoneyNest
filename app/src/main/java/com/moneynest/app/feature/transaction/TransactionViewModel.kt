
package com.moneynest.app.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneynest.app.data.local.entity.TransactionEntity
import com.moneynest.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _saveState =
        MutableStateFlow<SaveState>(SaveState.Idle)

    val saveState: StateFlow<SaveState> = _saveState

    fun saveTransaction(
        title: String,
        category: String,
        amount: Long,
        type: String
    ) {
        if (_saveState.value == SaveState.Loading ||
            _saveState.value == SaveState.Success
        ) return

        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                val transaction = TransactionEntity(
                    title = title,
                    category = category,
                    amount = amount,
                    type = type
                )

                repository.insertTransaction(transaction)

                _saveState.value = SaveState.Success

            } catch (e: Exception) {
                _saveState.value = SaveState.Error(
                    e.message ?: "Terjadi kesalahan"
                )
            }
        }
    }
}

sealed class SaveState {
    data object Idle : SaveState()
    data object Loading : SaveState()
    data object Success : SaveState()
    data class Error(val message: String) : SaveState()
}
