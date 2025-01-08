package de.tadris.contracts.sample.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import de.tadris.contracts.sample.ui.screens.ContractInfo

@Composable
fun ContractList(displayedContracts: List<ContractInfo>){
    LazyColumn {
        items(displayedContracts){ contractInfo ->
            ContractElement(contractInfo)
            HorizontalDivider()
        }
    }
}