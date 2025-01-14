package de.tadris.contracts.sample.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.tadris.contracts.sample.R
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import de.tadris.contracts.sample.ui.theme.spacingMin

@Composable
fun LauncherScreen(onNameChoose: (String) -> Unit){
    var name by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().padding(top = 60.dp), contentAlignment = Alignment.TopCenter){
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text(
                text = stringResource(R.string.choose_name),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.choose_name_hint),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.padding(top = spacingMin).fillMaxWidth(),
            )
            Button(onClick = {
                onNameChoose(name)
            }, modifier = Modifier.padding(top = spacingMin)) {
                Text(text = stringResource(R.string.action_submit))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LauncherScreenPreview(){
    SharkContractsSampleTheme {
        LauncherScreen { }
    }
}