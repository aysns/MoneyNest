
package com.moneynest.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moneynest.app.data.local.dao.TransactionDao
import com.moneynest.app.data.local.dao.TransactionItemDao
import com.moneynest.app.data.local.entity.TransactionEntity
import com.moneynest.app.data.local.entity.TransactionItemEntity

@Database(
    entities = [
        TransactionEntity::class,
        TransactionItemEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class MoneyNestDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun transactionItemDao(): TransactionItemDao

    companion object {

        val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {
                db.execSQL(
                    """
                    ALTER TABLE transactions
                    ADD COLUMN totalDiscount INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
            }
        }
    }
}
