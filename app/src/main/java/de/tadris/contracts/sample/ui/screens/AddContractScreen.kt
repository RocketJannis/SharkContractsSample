package de.tadris.contracts.sample.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.tadris.contracts.sample.R
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import net.sharksystem.contracts.content.ContractContent
import net.sharksystem.contracts.content.TextContent

@Composable
fun AddContractScreen(onContentChoose: (ContractContent) -> Unit){
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    Column {
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
            onClick = { onContentChoose(TextContent(title, text)) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(text = stringResource(R.string.action_create))
        }
    }
}

@Composable
@Preview
fun AddContractScreenPreview(){
    SharkContractsSampleTheme {
        AddContractScreen {  }
    }
}
