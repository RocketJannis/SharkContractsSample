package de.tadris.contracts.sample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import de.tadris.contracts.sample.ui.screens.MainScreenViewModel
import de.tadris.contracts.sample.ui.screens.SampleAppNavigation
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import net.sharksystem.SharkPeerFS
import net.sharksystem.asap.android.Util
import net.sharksystem.asap.android.apps.ASAPActivity
import net.sharksystem.asap.android.apps.ASAPAndroidPeer
import net.sharksystem.contracts.SharkContracts
import net.sharksystem.contracts.SharkContractsFactory
import net.sharksystem.contracts.content.ContractContent
import net.sharksystem.contracts.content.ContractContents
import net.sharksystem.contracts.content.ContractContentsFactory
import net.sharksystem.contracts.storage.TemporaryInMemoryStorage
import net.sharksystem.contracts.content.TextContent
import net.sharksystem.pki.SharkPKIComponent
import net.sharksystem.pki.SharkPKIComponentFactory
import kotlin.random.Random


class MainActivity : ASAPActivity() {

    private lateinit var contracts: SharkContracts
    private lateinit var contents: ContractContents
    private lateinit var viewModel: MainScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        initApplication()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val viewModel: MainScreenViewModel by viewModels()
        this.viewModel = viewModel
        findViewById<ComposeView>(R.id.composeRoot).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SharkContractsSampleTheme {
                    SampleAppNavigation(viewModel, this@MainActivity::createContract)
                }
            }
        }

        updateContentState()
    }

    private fun createContract(content: ContractContent){
        val packed = contents.pack(content)
        contracts.createContract(packed, emptyList(), false) // TODO change default values

        updateContentState()
    }

    private fun updateContentState(){
        viewModel.updateState(contracts, contents)
    }

    private fun initApplication(){
        val name = "anonymous-" + Random.nextInt(10000)
        val rootDir = Util.getASAPRootDirectory(this, "asap", name)
        val peer = SharkPeerFS(name, rootDir.absolutePath)


        // Add PKI
        val certificateComponentFactory = SharkPKIComponentFactory()
        peer.addComponent(certificateComponentFactory, SharkPKIComponent::class.java)
        val pki = peer.getComponent(SharkPKIComponent::class.java) as SharkPKIComponent

        // Add contracts
        val contractsFactory = SharkContractsFactory(pki, TemporaryInMemoryStorage())
        peer.addComponent(contractsFactory, SharkContracts::class.java)
        this.contracts = peer.getComponent(SharkContracts::class.java) as SharkContracts

        // Add content
        peer.addComponent(ContractContentsFactory(), ContractContents::class.java)
        this.contents = peer.getComponent(ContractContents::class.java) as ContractContents

        // Add supported content
        contents.registerType("text", TextContent::class.java)

        // Launch
        ASAPAndroidPeer.initializePeer(name, peer.supportedFormats, "sampleApplication", this)
        val asapPeer = ASAPAndroidPeer.startPeer(this)
        peer.start(asapPeer)

        startBluetooth()
        startBluetoothDiscoverable()
        startBluetoothDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopBluetooth()
    }

}