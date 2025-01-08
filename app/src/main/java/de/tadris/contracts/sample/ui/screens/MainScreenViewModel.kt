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
import net.sharksystem.contracts.content.ContractContent
import net.sharksystem.contracts.content.ContractContents

data class MainScreenState(val contractData: List<ContractInfo>)

data class ContractInfo(
    val contract: Contract,
    val content: ContentPackage?,
    val signatures: List<ContractSignature>,
    val state: ContractState
)

enum class ContractState {
    PENDING,
    VALID,
}

class MainScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState(emptyList()))
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    fun updateState(contracts: SharkContracts, content: ContractContents){
        _uiState.update {
            MainScreenState(
                contracts.listContracts().map { contract ->
                    ContractInfo(
                        contract,
                        try { content.extract(contract) } catch (e: Exception){ e.printStackTrace(); null },
                        contracts.listSignatures(contract),
                        if(contracts.isSignedByAllParties(contract)) ContractState.VALID else ContractState.PENDING
                    )
                }
            )
        }
    }

}