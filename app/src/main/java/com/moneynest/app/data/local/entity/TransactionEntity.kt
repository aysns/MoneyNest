
package com.moneynest.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String,
    val category: String,
    val amount: Long,
    val type: String,

    val transactionDate: String = "",

    val totalDiscount: Long = 0L
)
