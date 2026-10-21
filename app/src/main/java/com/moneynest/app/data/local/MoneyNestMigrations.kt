
package com.moneynest.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `transaction_items` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `transactionId` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `quantity` INTEGER NOT NULL,
                `unitPrice` INTEGER NOT NULL,
                `discount` INTEGER NOT NULL,
                FOREIGN KEY(`transactionId`)
                    REFERENCES `transactions`(`id`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            `index_transaction_items_transactionId`
            ON `transaction_items` (`transactionId`)
            """.trimIndent()
        )
    }
}


val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            ALTER TABLE `transactions`
            ADD COLUMN `transactionDate`
            TEXT NOT NULL DEFAULT ''
            """.trimIndent()
        )
    }
}

