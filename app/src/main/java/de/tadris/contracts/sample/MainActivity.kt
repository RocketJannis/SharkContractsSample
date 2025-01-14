package de.tadris.contracts.sample

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.room.Room
import de.tadris.contracts.sample.persistence.Settings
import de.tadris.contracts.sample.ui.persistence.AppDatabase
import de.tadris.contracts.sample.ui.persistence.RoomContractStorage
import de.tadris.contracts.sample.ui.screens.ContractBuildData
import de.tadris.contracts.sample.ui.screens.MainViewModel
import de.tadris.contracts.sample.ui.screens.SampleAppNavigation
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme
import net.sharksystem.SharkPeerFS
import net.sharksystem.asap.android.Util
import net.sharksystem.asap.android.apps.ASAPActivity
import net.sharksystem.asap.android.apps.ASAPAndroidPeer
import net.sharksystem.contracts.Contract
import net.sharksystem.contracts.ContractSignature
import net.sharksystem.contracts.ContractsListener
import net.sharksystem.contracts.SharkContracts
import net.sharksystem.contracts.SharkContractsFactory
import net.sharksystem.contracts.content.ContractContents
import net.sharksystem.contracts.content.ContractContentsFactory
import net.sharksystem.contracts.content.TextContent
import net.sharksystem.hub.peerside.TCPHubConnectorDescriptionImpl
import net.sharksystem.pki.CredentialMessage
import net.sharksystem.pki.SharkPKIComponent
import net.sharksystem.pki.SharkPKIComponentFactory
import kotlin.random.Random


class MainActivity : ASAPActivity(), ContractsListener {

    private lateinit var db: AppDatabase
    private lateinit var pki: SharkPKIComponent
    private lateinit var contracts: SharkContracts
    private lateinit var contents: ContractContents
    private lateinit var viewModel: MainViewModel
    private lateinit var settings: Settings
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        initApplication()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val viewModel: MainViewModel by viewModels()
        this.viewModel = viewModel
        findViewById<ComposeView>(R.id.composeRoot).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SharkContractsSampleTheme {
                    SampleAppNavigation(
                        viewModel,
                        this@MainActivity::createContract,
                        this@MainActivity::signContract
                    )
                }
            }
        }

        updateContentState()
    }

    private fun createContract(data: ContractBuildData){
        val packed = contents.pack(data.content)
        contracts.createContract(packed, data.parties, data.encrypted)

        updateContentState()
    }

    private fun signContract(contract: Contract){
        contracts.signContract(contract)

        updateContentState()
    }

    private fun updateContentState(){
        viewModel.updateState(pki, contracts, contents)
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
        pki = peer.getComponent(SharkPKIComponent::class.java) as SharkPKIComponent

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
        ASAPAndroidPeer.initializePeer(name, peer.formats, "sampleApplication", this)
        peer.start(ASAPAndroidPeer.startPeer(this))

        // automatically exchange credentials
        pki.setBehaviour(SharkPKIComponent.BEHAVIOUR_SEND_CREDENTIAL_FIRST_ENCOUNTER, true)

        // auto accept credentials in this example
        pki.setSharkCredentialReceivedListener(this::onSharkCredentialReceived)

        handler.postDelayed({
            startBluetooth()
            startBluetoothDiscovery()
            startBluetoothDiscoverable()

            val hub = TCPHubConnectorDescriptionImpl("relite.fritz.box", 6907, true)
            peer.addHubDescription(hub)
            hubConnectionManager.connectHub(hub)
        }, 5000)
    }

    private fun onSharkCredentialReceived(credentialMessage: CredentialMessage){
        Log.d("MainActivity", "Received Credentials: $credentialMessage")
        try {
            handler.postDelayed({
                if(try { pki.getCertificatesBySubject(credentialMessage.subjectID).isEmpty() } catch (e: Exception){ true }){
                    Log.d("MainActivity", "New credentials, accept and sign: $credentialMessage")
                    pki.acceptAndSignCredential(credentialMessage)
                    updateContentState()
                }else{
                    Log.d("MainActivity", "Credentials known, certificate exists: $credentialMessage")
                }
            }, 5000)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopBluetooth()
    }

    override fun onContractReceived(contract: Contract) {
        updateContentState()
    }

    override fun onSignatureReceived(signature: ContractSignature) {
        updateContentState()
    }

}