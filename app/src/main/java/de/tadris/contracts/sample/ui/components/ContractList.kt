package de.tadris.contracts.sample.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import de.tadris.contracts.sample.ui.screens.ContractInfo
import net.sharksystem.contracts.Contract

@Composable
fun ContractList(displayedContracts: List<ContractInfo>, onSign: (Contract) -> Unit){
    LazyColumn {
        items(displayedContracts){ contractInfo ->
            ContractElement(contractInfo){ onSign(contractInfo.contract) }
            HorizontalDivider()
        }
    }
}