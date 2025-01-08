package de.tadris.contracts.sample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.tadris.contracts.sample.R
import de.tadris.contracts.sample.ui.screens.ContractInfo
import de.tadris.contracts.sample.ui.screens.ContractState
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import de.tadris.contracts.sample.ui.theme.spacingMin
import net.sharksystem.contracts.Contract
import net.sharksystem.contracts.ContractParty
import net.sharksystem.contracts.content.ContentPackage
import net.sharksystem.contracts.content.TextContent
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ContractElement(info: ContractInfo){
    val contract = info.contract

    Column(modifier = Modifier.padding(spacingMin)) {
        Text(text = stringResource(R.string.contract_author, contract.hash.take(6), contract.authorId) + " (" + info.state.toString() + ")")
        if(info.content != null){
            ContentElement(info.content)
        }else{
            NoContentElement()
        }
    }
}

@Composable
fun ContentElement(contentPackage: ContentPackage){
    val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
    Text(text = dateFormat.format(contentPackage.date))
    when(val content = contentPackage.content){
        is TextContent -> TextContentView(content)
    }
}

@Composable
fun NoContentElement(){
    Text(text = stringResource(R.string.contract_content_unknown))
}

@Composable
fun TextContentView(content: TextContent){
    Text(text = content.title, style = MaterialTheme.typography.titleLarge)
    Text(text = content.text)
}

@Preview(showBackground = true)
@Composable
fun ContractPreview(){
    SharkContractsSampleTheme {
        val content = TextContent("Title", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et")
        val contentPackage = ContentPackage("text", Date(), content)
        ContractElement(
            ContractInfo(
                Contract("alice", byteArrayOf(), listOf(ContractParty("bob", byteArrayOf())), false, "a", byteArrayOf()),
                contentPackage,
                emptyList(),
                ContractState.VALID,
            )
        )
    }
}