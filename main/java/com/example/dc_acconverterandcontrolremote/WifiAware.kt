@file:OptIn(ExperimentalUnsignedTypes::class)

package com.example.dc_acconverterandcontrolremote

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.MacAddress
import android.net.wifi.aware.Characteristics.WIFI_AWARE_CIPHER_SUITE_NCS_PK_128
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareDataPathSecurityConfig
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalUnsignedTypes::class)
class WifiAware(val context: Context, val viewModel: DeviceSchedulerViewModel) {

private final val MAC_ADDRESS_MESSAGE: Int = 55

lateinit var config: PublishConfig

lateinit var wifiAwareSession: WifiAwareSession

lateinit var  wifiAwareManager: WifiAwareManager

lateinit    var secConfig: WifiAwareDataPathSecurityConfig

lateinit var subscribeConfig: SubscribeConfig

lateinit var subscribeDiscoverySession: SubscribeDiscoverySession

lateinit var  peerHandle: PeerHandle

 var portToUse : Int?=null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun discover(): Intent? {

        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)) {


            wifiAwareManager = context.getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager

            val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)

            val myReceiver = AwareBroadcastReceiver(context, viewModel, wifiAwareManager)

            return context.registerReceiver(myReceiver, filter)
        }else return null
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    fun publish(pskOrPmk:String?, isPmk: Boolean, serviceName: String, serviceInfo: String){
        // psk is 8 bits code as string
        // pmk is 32 characters alphanumeric code
        val pmkid = ByteArray(16)
        if (pskOrPmk!=null){
            if (isPmk) {
                    secConfig =
                        WifiAwareDataPathSecurityConfig.Builder(
                            WIFI_AWARE_CIPHER_SUITE_NCS_PK_128
                        )
                            .setPmk(pskOrPmk.toByteArray())
                            .setPmkId(pmkid)
                            .build()

            }else {
                    secConfig = WifiAwareDataPathSecurityConfig.Builder(
                        WIFI_AWARE_CIPHER_SUITE_NCS_PK_128
                    )
                        .setPskPassphrase(pskOrPmk)
                        .build();


            }
                config = PublishConfig.Builder()
                    .setServiceName(serviceName)
                    .setServiceSpecificInfo(serviceInfo.toByteArray())
                    .setDataPathSecurityConfig(secConfig)
                    .setPublishType(PublishConfig.PUBLISH_TYPE_SOLICITED)
                    .build()



        }
        else{
                config = PublishConfig.Builder()
                    .setServiceName(serviceName)
                    .setServiceSpecificInfo(serviceInfo.toByteArray())
                    .setPublishType(PublishConfig.PUBLISH_TYPE_SOLICITED)
                    .build()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun susbcribe (serviceName: String, serviceInfo: String, macAddress: String){

            subscribeConfig = SubscribeConfig.Builder()
                .setServiceName(serviceName)
                .setServiceSpecificInfo(serviceInfo.toByteArray())
                .setSubscribeType(SubscribeConfig.SUBSCRIBE_TYPE_ACTIVE)
                .build()

            wifiAwareSession.subscribe(subscribeConfig, object : DiscoverySessionCallback() {

                override fun onServiceDiscovered(peerHandle: PeerHandle, serviceSpecificInfo: ByteArray , matchFilter: List<ByteArray> ) {
                    super.onServiceDiscovered(peerHandle, serviceSpecificInfo, matchFilter)

                    this@WifiAware.peerHandle = peerHandle

                    if (subscribeDiscoverySession != null && peerHandle != null) {
                        subscribeDiscoverySession.sendMessage(peerHandle, MAC_ADDRESS_MESSAGE, macAddress.toByteArray());
            }

    }



                override fun  onSubscribeStarted(session: SubscribeDiscoverySession ) {
                    super.onSubscribeStarted(session);

                    subscribeDiscoverySession = session;

                    subscribeDiscoverySession.sendMessage(peerHandle, MAC_ADDRESS_MESSAGE, macAddress.toByteArray());
                    println("onServiceStarted sending mac...");
                }


                override fun onMessageReceived(peerHandle : PeerHandle , message: ByteArray ) {
                    super.onMessageReceived(peerHandle, message)
                    if (message.size == 2) {
                        portToUse =  viewModel.setPortToUse(message);
                        println("received message, port = $portToUse")

                    } else if (message.size == 6) {
                        viewModel.setMacAddress(message);

                    } else if (message.size == 16) {;
                        viewModel.setIpAddress(message);

                    } else if (message.size > 16) {
                        viewModel.receivedMessage(message);
                        //Toast.makeText(MainActivity.this, "message received", Toast.LENGTH_SHORT).show();
                    }
                }
    }


                , null)
}
    }

