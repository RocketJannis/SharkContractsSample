package de.tadris.contracts.sample.ui.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contractDao(): ContractDao

}