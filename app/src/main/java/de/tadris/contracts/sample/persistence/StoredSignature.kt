package de.tadris.contracts.sample.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signature")
data class StoredSignature(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "contract_hash") val contractHash: String,
    val author: String,
    val signature: ByteArray,
)