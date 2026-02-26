package com.example.dc_acconverterandcontrolremote

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.PublishDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.lifecycle.viewmodel.compose.viewModel


class WifiAware(context: Context) {

lateinit var config: PublishConfig
lateinit var wifiAwareSession: WifiAwareSession

lateinit var  wifiAwareManager: WifiAwareManager



fun discover(context: Context, viewModel: DeviceSchedulerViewModel): Intent? {

    if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)) {


        wifiAwareManager = context.getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager

        val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)

        val myReceiver = AwareBroadcastReceiver(context, viewModel, wifiAwareManager)

        return context.registerReceiver(myReceiver, filter)
    }else return null
}


        @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
        fun publish(){

            config= PublishConfig.Builder()
                .setServiceName(AWARE_FILE_SHARE_SERVICE_NAME)
                .build()
            wifiAwareSession.publish(config, object : DiscoverySessionCallback() {
                override fun onPublishStarted(session: PublishDiscoverySession) {
                }
                override fun onMessageReceived(peerHandle: PeerHandle, message: ByteArray{
                }
        }


    }