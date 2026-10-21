
package com.moneynest.app.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneynest.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CategoryExpense(
    val category: String,
    val amount: Long
)

data class AnalyticsUiState(
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val balance: Long = 0L,
    val categoryExpenses: List<CategoryExpense> = emptyList()
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> =
        repository.getAllTransaction()
            .map { transactions ->

                // Hitung total pemasukan
                val totalIncome = transactions
                    .filter { it.type == "INCOME" }
                    .sumOf { it.amount }

                // Ambil semua pengeluaran
                val expenses = transactions
                    .filter { it.type == "EXPENSE" }

                // Hitung total pengeluaran
                val totalExpense = expenses
                    .sumOf { it.amount }

                // Hitung saldo
                val balance = totalIncome - totalExpense

                // Kelompokkan pengeluaran berdasarkan kategori
                val categoryExpenses = expenses
                    .groupBy { it.category }
                    .map { (category, items) ->
                        CategoryExpense(
                            category = category,
                            amount = items.sumOf { it.amount }
                        )
                    }
                    .sortedByDescending { it.amount }

                AnalyticsUiState(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = balance,
                    categoryExpenses = categoryExpenses
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AnalyticsUiState()
            )
}
