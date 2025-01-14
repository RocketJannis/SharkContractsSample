package de.tadris.contracts.sample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import de.tadris.contracts.sample.persistence.Settings
import de.tadris.contracts.sample.ui.screens.LauncherScreen
import de.tadris.contracts.sample.ui.theme.SharkContractsSampleTheme

class LauncherActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private val neededPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_ADVERTISE,
            android.Manifest.permission.BLUETOOTH_SCAN,
        )
    } else {
        listOf(
            android.Manifest.permission.BLUETOOTH_ADMIN
        )
    } + listOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<ComposeView>(R.id.composeRoot).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SharkContractsSampleTheme {
                    LauncherScreen(this@LauncherActivity::chooseName)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(findNotGrantedPermissions().isNotEmpty()){
            requestPermissions(findNotGrantedPermissions().toTypedArray(), PERMISSION_REQUEST_CODE)
        }else if(Settings(this).ownerName.isNotEmpty()){
            startApplication()
        }
    }

    private fun chooseName(name: String){
        Settings(this).ownerName = name // save name
        startApplication()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if (resultCode != PackageManager.PERMISSION_GRANTED) {
                finish()
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startApplication(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun findNotGrantedPermissions() = neededPermissions.filter {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }

}