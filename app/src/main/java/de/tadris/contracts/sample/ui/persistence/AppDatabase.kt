package de.tadris.contracts.sample.ui.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StoredContract::class, StoredSignature::class, StoredContractParty::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contractDao(): ContractDao

}