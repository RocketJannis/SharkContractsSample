package de.tadris.contracts.sample

import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.room.Room
import de.tadris.contracts.sample.persistence.Settings
import de.tadris.contracts.sample.ui.persistence.AppDatabase
import de.tadris.contracts.sample.ui.persistence.RoomContractStorage
import de.tadris.contracts.sample.ui.screens.MainScreenViewModel
import de.tadris.contracts.sample.ui.screens.SampleAppNavigation
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import net.sharksystem.SharkPeerFS
import net.sharksystem.asap.ASAPSecurityException
import net.sharksystem.asap.android.Util
import net.sharksystem.asap.android.apps.ASAPActivity
import net.sharksystem.asap.android.apps.ASAPAndroidPeer
import net.sharksystem.contracts.Contract
import net.sharksystem.contracts.ContractSignature
import net.sharksystem.contracts.ContractsListener
import net.sharksystem.contracts.SharkContracts
import net.sharksystem.contracts.SharkContractsFactory
import net.sharksystem.contracts.content.ContractContent
import net.sharksystem.contracts.content.ContractContents
import net.sharksystem.contracts.content.ContractContentsFactory
import net.sharksystem.contracts.content.TextContent
import net.sharksystem.contracts.storage.TemporaryInMemoryStorage
import net.sharksystem.hub.peerside.TCPHubConnectorDescriptionImpl
import net.sharksystem.pki.CredentialMessage
import net.sharksystem.pki.SharkPKIComponent
import net.sharksystem.pki.SharkPKIComponentFactory
import java.io.IOException
import kotlin.random.Random


class MainActivity : ASAPActivity() {

    private val LOCK = Any()
    private lateinit var db: AppDatabase
    private lateinit var contracts: SharkContracts
    private lateinit var contents: ContractContents
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var settings: Settings

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
        settings = Settings(this)
        if(settings.ownerName.isEmpty()){
            settings.ownerName = "anonymous-" + Random.nextInt(10000)
        }
        val name = settings.ownerName
        val rootDir = Util.getASAPRootDirectory(this, "asap", name)
        val peer = SharkPeerFS(name, rootDir.absolutePath)

        // Init DB
        db = Room.databaseBuilder(this, AppDatabase::class.java, "app-database.db")
            .allowMainThreadQueries()
            .build()

        // Add PKI
        val certificateComponentFactory = SharkPKIComponentFactory()
        peer.addComponent(certificateComponentFactory, SharkPKIComponent::class.java)
        val pki = peer.getComponent(SharkPKIComponent::class.java) as SharkPKIComponent

        // Add contracts
        val contractsFactory = SharkContractsFactory(pki, RoomContractStorage(db.contractDao()))
        peer.addComponent(contractsFactory, SharkContracts::class.java)
        this.contracts = peer.getComponent(SharkContracts::class.java) as SharkContracts
        contracts.registerListener(this)

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
        // auto accept credentials in this example
        pki.setSharkCredentialReceivedListener { credentialMessage: CredentialMessage ->
            synchronized(LOCK){
                println("MainActivity - RECEIVED CREDENTIALS: $credentialMessage")
                try {
                    pki.acceptAndSignCredential(credentialMessage)
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }

        Handler().postDelayed({
            startBluetooth()
            startBluetoothDiscoverable()
            startBluetoothDiscovery()

            val hub = TCPHubConnectorDescriptionImpl("relite.fritz.box", 6907, true)
            peer.addHubDescription(hub)
            hubConnectionManager.connectHub(hub)
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopBluetooth()
    }

}