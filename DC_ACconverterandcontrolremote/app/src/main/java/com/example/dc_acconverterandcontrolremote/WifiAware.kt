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
import androidx.annotation.RequiresPermission


class WifiAware(context: Context) {

lateinit var config: PublishConfig
lateinit var wifiAwareSession: WifiAwareSession


    fun discover(context: Context): Intent?{

    if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)) {
        val wifiAwareManager = context.getSystemService(Context.WIFI_AWARE_SERVICE)
        val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)
        val myReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                if (wifiAwareManager?.isAvailable) {

                } else {

                }
            }
        }
     return context.registerReceiver(myReceiver, filter)
    }

      return null
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