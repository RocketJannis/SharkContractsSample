package de.tadris.contracts.sample.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.tadris.contracts.sample.R
import net.sharksystem.contracts.Contract

enum class Screen(@StringRes val titleRes: Int) {
    MAIN(R.string.app_name),
    ADD_CONTRACT(R.string.action_add_contract),
}

@Composable
fun SampleAppNavigation(
    viewModel: MainViewModel,
    createContract: (ContractBuildData) -> Unit,
    onSign: (Contract) -> Unit
){
    var currentScreen by rememberSaveable { mutableStateOf(Screen.MAIN) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SampleAppBar(currentScreen){
                currentScreen = it
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Crossfade(currentScreen, animationSpec = tween(300), label = "Content crossfade") { displayedScreen ->
                when(displayedScreen){
                    Screen.MAIN -> MainScreen(viewModel, onSign = onSign)
                    Screen.ADD_CONTRACT -> AddContractScreen(viewModel) {
                        currentScreen = Screen.MAIN
                        createContract(it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleAppBar(currentScreen: Screen, navigate: (Screen) -> Unit){
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Crossfade(currentScreen, label = "Title") {
                Text(text = stringResource(it.titleRes))
            }
        },
        actions = {
            AnimatedVisibility(currentScreen == Screen.MAIN) {
                IconButton(onClick = { navigate(Screen.ADD_CONTRACT) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.action_add_contract)
                    )
                }
            }
        },
        navigationIcon = {
            AnimatedVisibility(
                visible = currentScreen != Screen.MAIN,
                enter = fadeIn() + expandHorizontally(),
                exit = shrinkHorizontally() + fadeOut(),
            ) {
                IconButton(onClick = { navigate(Screen.MAIN) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        }
    )
}