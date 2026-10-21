
package com.moneynest.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.moneynest.app.data.local.entity.TransactionItemEntity

@Dao
interface TransactionItemDao {

    @Insert
    suspend fun insertItems(
        items: List<TransactionItemEntity>
    )

    @Query(
        "SELECT * FROM transaction_items " +
                "WHERE transactionId = :transactionId"
    )
    suspend fun getItemsByTransactionId(
        transactionId: Long
    ): List<TransactionItemEntity>
}
