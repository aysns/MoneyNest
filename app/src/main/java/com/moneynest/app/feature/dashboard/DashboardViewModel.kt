
package com.moneynest.app.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneynest.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.moneynest.app.data.local.entity.TransactionEntity

data class DashboardUiState(
    val balance: Long = 0L,
    val income: Long = 0L,
    val expense: Long = 0L,
    val monthlyExpense: Long = 0L,
    val recentTransactions: List<TransactionEntity> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        repository.getAllTransaction()
            .map { transactions ->

                val income = transactions
                    .filter { it.type == "INCOME" }
                    .sumOf { it.amount }

                val expense = transactions
                    .filter { it.type == "EXPENSE" }
                    .sumOf { it.amount }


                val currentMonth = Calendar.getInstance()

                val dateFormat = SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                ).apply {
                    isLenient = false
                }

                val monthlyExpense = transactions
                    .filter { it.type == "EXPENSE" }
                    .filter { transaction ->
                        val date = runCatching {
                            dateFormat.parse(transaction.transactionDate)
                        }.getOrNull()

                        if (date == null) {
                            false
                        } else {
                            val transactionCalendar = Calendar.getInstance().apply {
                                time = date
                            }

                            transactionCalendar.get(Calendar.MONTH) ==
                                    currentMonth.get(Calendar.MONTH) &&
                                    transactionCalendar.get(Calendar.YEAR) ==
                                    currentMonth.get(Calendar.YEAR)
                        }
                    }
                    .sumOf { it.amount }

                val recentTransactions = transactions
                    .sortedByDescending { it.id }
                    .take(3)

                DashboardUiState(
                    balance = income - expense,
                    income = income,
                    expense = expense,
                    monthlyExpense = monthlyExpense,
                    recentTransactions = recentTransactions
                )

            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DashboardUiState()
            )
}
