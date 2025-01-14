package de.tadris.contracts.sample.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import de.tadris.contracts.sample.ui.components.ContractList
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import net.sharksystem.contracts.Contract

@Composable
fun MainScreen(viewModel: MainViewModel, onSign: (Contract) -> Unit){
    val mainScreenState = viewModel.uiState.collectAsState(MainScreenState.empty)
    MainScreenContent(
        mainScreenState.value,
        onSign = onSign,
    )
}

@Composable
fun MainScreenContent(state: MainScreenState, onSign: (Contract) -> Unit){
    ContractList(state.contractData, onSign)
}

@Preview
@Composable
fun MainScreenPreview(){
    SharkContractsSampleTheme {
        val viewModel = MainViewModel()
        MainScreen(viewModel){ }
    }
}


