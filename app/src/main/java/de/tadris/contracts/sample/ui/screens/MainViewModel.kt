package de.tadris.contracts.sample.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.sharksystem.contracts.Contract
import net.sharksystem.contracts.ContractSignature
import net.sharksystem.contracts.SharkContracts
import net.sharksystem.contracts.content.ContentPackage
import net.sharksystem.contracts.content.ContractContents
import net.sharksystem.pki.SharkPKIComponent

data class MainScreenState(val contractData: List<ContractInfo>, val knownPeers: List<String>){

    companion object {
        val empty = MainScreenState(emptyList(), emptyList())
    }

}

data class ContractInfo(
    val contract: Contract,
    val content: ContentPackage?,
    val signatures: List<ContractSignature>,
    val state: ContractState,
    val signable: Boolean,
)

enum class ContractState {
    PENDING,
    VALID,
}

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState.empty)
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    fun updateState(pki: SharkPKIComponent, contracts: SharkContracts, content: ContractContents){
        val myId = pki.ownerID

        _uiState.update {
            MainScreenState(
                contracts.listContracts().reversed().map { contract ->
                    val signatures = contracts.listSignatures(contract)
                    val contractByMe = contract.authorId == myId
                    val signedByMe = contractByMe || signatures.find { it.author == myId } != null

                    ContractInfo(
                        contract,
                        try { content.extract(contract) } catch (e: Exception){ e.printStackTrace(); null },
                        signatures,
                        if(contracts.isSignedByAllParties(contract)) ContractState.VALID else ContractState.PENDING,
                        contract.otherPartyIds.contains(myId) && !signedByMe,
                    )
                },
                contracts.knownPeers
            )
        }
    }

}