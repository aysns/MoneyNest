package com.moneynest.app.data.repository

import com.moneynest.app.data.local.dao.TransactionDao
import com.moneynest.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    fun getAllTransaction(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    suspend fun insertTransaction(
        transaction: TransactionEntity
    ) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(
        transaction: TransactionEntity
    ) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(
        transaction: TransactionEntity
    ) {
        transactionDao.deleteTransaction(transaction)
    }
}