
package com.moneynest.app.di

import android.content.Context
import androidx.room.Room
import com.moneynest.app.data.local.MoneyNestDatabase
import com.moneynest.app.data.local.MIGRATION_1_2
import com.moneynest.app.data.local.MIGRATION_2_3
import com.moneynest.app.data.local.dao.TransactionDao
import com.moneynest.app.data.local.dao.TransactionItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MoneyNestDatabase {

        return Room.databaseBuilder(
            context,
            MoneyNestDatabase::class.java,
            "moneynest_database"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MoneyNestDatabase.MIGRATION_3_4
            )
            .build()
    }

    @Provides
    fun provideTransactionDao(
        database: MoneyNestDatabase
    ): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideTransactionItemDao(
        database: MoneyNestDatabase
    ): TransactionItemDao {
        return database.transactionItemDao()
    }
}
