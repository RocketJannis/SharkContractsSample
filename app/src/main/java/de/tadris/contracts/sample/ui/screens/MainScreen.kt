package de.tadris.contracts.sample.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.tadris.contracts.sample.R
import de.tadris.contracts.sample.ui.components.ContractList
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme

@Composable
fun MainScreen(viewModel: MainScreenViewModel){
    val mainScreenState = viewModel.uiState.collectAsState(MainScreenState(emptyList()))
    MainScreenContent(
        onNewContractClicked = { },
        mainScreenState.value,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(onNewContractClicked: () -> Unit, state: MainScreenState){
    ContractList(state.contractData)
}

@Preview
@Composable
fun MainScreenPreview(){
    SharkContractsSampleTheme {
        val viewModel = MainScreenViewModel()
        MainScreen(viewModel)
    }
}


