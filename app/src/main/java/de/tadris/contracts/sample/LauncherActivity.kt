package de.tadris.contracts.sample

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class LauncherActivity : Activity() {

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

    override fun onResume() {
        super.onResume()
        if(findNotGrantedPermissions().isEmpty()){
            startApplication()
        }else{
            requestPermissions(findNotGrantedPermissions().toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(resultCode == PackageManager.PERMISSION_GRANTED){
                startApplication()
            }else{
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