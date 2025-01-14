package de.tadris.contracts.sample.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import de.tadris.contracts.sample.R
import de.tadris.contracts.sample.ui.theme.spacingMin

@Composable
fun ContractPartyChip(
    party: String,
    background: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: (() -> Unit)?
){
    InputChip(
        onClick = {
            onClick?.invoke()
        },
        colors = InputChipDefaults.inputChipColors().copy(
            selectedContainerColor = background,
        ),
        label = { Text(party) },
        selected = true,
        avatar = {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
        trailingIcon = {
            if(onClick != null){
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.action_remove),
                    Modifier.size(InputChipDefaults.AvatarSize)
                )
            }
        },
        modifier = Modifier.padding(horizontal = spacingMin),
    )
}