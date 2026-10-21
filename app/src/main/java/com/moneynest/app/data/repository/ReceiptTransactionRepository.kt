
package com.moneynest.app.data.repository

import androidx.room.withTransaction
import com.moneynest.app.data.local.MoneyNestDatabase
import com.moneynest.app.data.local.entity.TransactionEntity
import com.moneynest.app.data.local.entity.TransactionItemEntity
import javax.inject.Inject

class ReceiptTransactionRepository @Inject constructor(
    private val database: MoneyNestDatabase
) {

    suspend fun saveReceipt(
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>
    ): Long {
        return database.withTransaction {

            val transactionId = database
                .transactionDao()
                .insertTransaction(transaction)

            val itemsWithTransactionId = items.map { item ->
                item.copy(
                    transactionId = transactionId
                )
            }

            database
                .transactionItemDao()
                .insertItems(itemsWithTransactionId)

            transactionId
        }
    }

    suspend fun getReceiptItems(
        transactionId: Long
    ): List<TransactionItemEntity> {
        return database
            .transactionItemDao()
            .getItemsByTransactionId(transactionId)
    }

}
