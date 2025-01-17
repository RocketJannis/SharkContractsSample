package de.tadris.contracts.sample.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contract_party")
data class StoredContractParty(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "contract_hash") val contractHash: String,
    val name: String,
    @ColumnInfo(name = "encrypted_key") val encryptedKey: ByteArray,
)