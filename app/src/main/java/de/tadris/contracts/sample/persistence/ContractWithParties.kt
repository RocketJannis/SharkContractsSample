package de.tadris.contracts.sample.persistence

import androidx.room.Embedded
import androidx.room.Relation

data class ContractWithParties(
    @Embedded val contract: StoredContract,
    @Relation(
        parentColumn = "hash",
        entityColumn = "contract_hash"
    )
    val parties: List<StoredContractParty>,
)