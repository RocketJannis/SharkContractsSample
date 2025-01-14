package de.tadris.contracts.sample.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.tadris.contracts.sample.R
import de.tadris.contracts.sample.ui.components.ContractPartyChip
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import de.tadris.contracts.sample.ui.theme.cornerRad
import de.tadris.contracts.sample.ui.theme.spacingMin
import net.sharksystem.contracts.content.ContractContent
import net.sharksystem.contracts.content.TextContent

data class ContractBuildData(
    val parties: List<String>,
    val content: ContractContent,
    val encrypted: Boolean,
)

@Composable
fun AddContractScreen(viewModel: MainViewModel, onContentChoose: (ContractBuildData) -> Unit){
    val mainScreenState = viewModel.uiState.collectAsState(MainScreenState.empty)

    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var parties by remember { mutableStateOf(emptyList<String>()) }
    var encrypted by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(spacingMin)) {
        ContractParties(
            mainScreenState.value.knownPeers,
            parties,
            onRemove = {
                parties = parties - listOf(it).toSet()
            },
            onAdd = {
                parties = parties + listOf(it)
            }
        )
        EncryptedCheckbox (
            state = encrypted,
            onStateChange = { encrypted = it },
        )
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.contract_title)) },
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.contract_text)) },
            singleLine = false,
            minLines = 5,
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = { onContentChoose(ContractBuildData(
                parties,
                TextContent(title, text),
                encrypted
            )) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(text = stringResource(R.string.action_create))
        }
    }
}

@Composable
fun EncryptedCheckbox(state: Boolean, onStateChange: (Boolean) -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onStateChange(!state) }
            .padding(vertical = spacingMin)
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = null,
        )
        Text(
            text = stringResource(R.string.contract_create_encrypted),
            modifier = Modifier.padding(start = spacingMin),
        )
    }
}

@Composable
fun ContractParties(selectableParties: List<String>, selectedParties: List<String>, onRemove: (String) -> Unit, onAdd: (String) -> Unit){
    var showDialog by remember { mutableStateOf(false) }
    val additionalParties = selectableParties - selectedParties.toSet()

    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
        selectedParties.forEach { party ->
            ContractPartyChip(party){
                onRemove(party)
            }
        }
        if(additionalParties.isNotEmpty()){
            InputChip(
                onClick = {
                    showDialog = true
                },
                label = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        Modifier.size(InputChipDefaults.AvatarSize)
                    )
                },
                selected = true,
            )
        }
    }

    if(showDialog){
        SelectPartyDialog(
            additionalParties,
            onDismiss = { showDialog = false },
            onChoose = {
                onAdd(it)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPartyDialog(parties: List<String>, onDismiss: () -> Unit, onChoose: (String) -> Unit){
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .background(AlertDialogDefaults.containerColor, shape = RoundedCornerShape(cornerRad))
            .padding(spacingMin)
    ) {
        Column {
            Text(
                text = stringResource(R.string.contract_party_add_title),
                style = MaterialTheme.typography.titleLarge
            )
            parties.forEach { party ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(spacingMin)
                        .fillMaxWidth()
                        .clickable { onChoose(party) }
                ) {
                    RadioButton(
                        selected = false,
                        onClick = null,
                    )
                    Text(party, modifier = Modifier.padding(start = spacingMin))
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddContractScreenPreview(){
    SharkContractsSampleTheme {
        AddContractScreen(
            viewModel = MainViewModel(),
            onContentChoose = { },
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AddContractScreenDialogPreview(){
    SharkContractsSampleTheme {
        SelectPartyDialog(listOf("Alice", "Bob"), {}, {})
    }
}
