package de.tadris.contracts.sample.ui.persistence

import net.sharksystem.contracts.Contract
import net.sharksystem.contracts.ContractParty
import net.sharksystem.contracts.ContractSignature
import net.sharksystem.contracts.storage.ContractStorage

class RoomContractStorage(private val dao: ContractDao) : ContractStorage {

    override fun insertContract(contract: Contract) {
        dao.insertContract(
            StoredContract(contract.hash, contract.authorId, contract.content, contract.isEncrypted, contract.signature)
        )
        contract.otherParties.forEach { party ->
            dao.insertParty(StoredContractParty(-1, contract.hash, party.id, party.encryptedKey))
        }
    }

    override fun insertSignature(signature: ContractSignature) {
        dao.insertSignature(
            StoredSignature(-1, signature.contractHash, signature.author, signature.signature)
        )
    }

    override fun loadAllContracts() = dao.findAllContracts().map { it.convert() }

    override fun findContract(hash: String) = dao.findContract(hash)?.convert()

    override fun findSignatures(contractHash: String) = dao.findSignatures(contractHash)
        .map { ContractSignature(it.contractHash, it.author, it.signature) }

    private fun ContractWithParties.convert(): Contract {
        val parties = parties.map { party ->
            ContractParty(party.name, party.encryptedKey)
        }
        return Contract(contract.authorId, contract.content, parties, contract.encrypted, contract.hash, contract.signature)
    }

}