package com.moneynest.app.domain.model

data class Transaction(
    val id: Long,
    val title: String,
    val category: String,
    val amount: Long,
    val type: TransactionType
)

enum class TransactionType {
    INCOME,
    EXPENSE
}