package de.tadris.contracts.sample.ui.persistence

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

interface ContractDao {

    @Insert
    fun insertContract(contract: StoredContract)

    @Insert
    fun insertParty(contractParty: StoredContractParty)

    @Insert
    fun insertSignature(signature: StoredSignature)

    @Transaction
    @Query("SELECT * from contract")
    fun findAllContracts(): List<ContractWithParties>

    @Query("SELECT * from contract where hash=:hash")
    fun findContract(hash: String): ContractWithParties?

    @Query("SELECT * from signature where contract_hash=:contractHash")
    fun findSignatures(contractHash: String): List<StoredSignature>


}