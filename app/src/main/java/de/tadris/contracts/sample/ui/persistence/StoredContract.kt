package de.tadris.contracts.sample.ui.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contract")
data class StoredContract(
    @PrimaryKey val hash: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    @ColumnInfo val content: ByteArray,
    @ColumnInfo val encrypted: Boolean,
    @ColumnInfo val signature: ByteArray,
)