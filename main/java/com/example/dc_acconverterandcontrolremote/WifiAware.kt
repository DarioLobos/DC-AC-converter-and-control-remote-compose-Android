@file:OptIn(ExperimentalUnsignedTypes::class)

package com.example.dc_acconverterandcontrolremote

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.Characteristics.WIFI_AWARE_CIPHER_SUITE_NCS_PK_128
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.IdentityChangedListener
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareDataPathSecurityConfig
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareNetworkInfo
import android.net.wifi.aware.WifiAwareNetworkSpecifier
import android.net.wifi.aware.WifiAwareSession
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.util.Enumeration
import kotlin.ByteArray
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@OptIn(ExperimentalUnsignedTypes::class)
class WifiAware(val context: Context, val viewModel: DeviceSchedulerViewModel) {

    private val MAC_ADDRESS_MESSAGE: Int = 55
    private val IP_ADDRESS_MESSAGE = 33

    var networkSpecifier: WifiAwareNetworkSpecifier? = null

    var config: PublishConfig? = null

    var wifiAwareSession: WifiAwareSession? = null

    var wifiAwareManager: WifiAwareManager? = null

    var secConfig: WifiAwareDataPathSecurityConfig? = null

    var subscribeConfig: SubscribeConfig? = null

    var subscribeDiscoverySession: SubscribeDiscoverySession? = null

    var peerHandle: PeerHandle? = null

    var portToUse: Int? = null

    var connectivityManager: ConnectivityManager? = null

    var socket: Socket? = null

    // pending definitions
    var serviceSpecificInfo: ByteArray? = null

    var matchFilter: List<ByteArray> = listOf(viewModel.getMatchFilterLaunch())


    var flagReceived: Boolean=false

    private val wifiMutex = Mutex()

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun attachToWifi() {

        if (wifiAwareSession != null) {
            return
        }

        if (wifiAwareManager == null || !wifiAwareManager!!.isAvailable()) {
            println("wifi unavailable")
            return;
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        wifiAwareManager!!.attach(object : AttachCallback() {
            override fun onAttached(session: WifiAwareSession) {
                super.onAttached(session)
                wifiAwareSession = session;
                println("wifi session successful")

            }

            override fun onAttachFailed() {
                super.onAttachFailed();
                wifiAwareSession!!.close()
                wifiAwareSession = null
                println("Aware session failed")
            }

        }, object : IdentityChangedListener() {
            @Override
            override fun onIdentityChanged(mac: ByteArray) {
                super.onIdentityChanged(mac)
                // In this program mac address remote should be set only one time
                viewModel.MacSetLaunchRemote(viewModel.setMacAddressToString(mac))
                println("New mac Address had been set ")
            }
        }, null);
    }

    // network request is done by te publisher and this program will work as suscriber
    // so only must send a message to esp32 start network
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun requestWifiNetwork() {
        if (networkSpecifier == null) {
            return;
        }

        val networkRequest: NetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
            .setNetworkSpecifier(networkSpecifier)
            .build()

        connectivityManager!!.requestNetwork(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network);
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val peerAwareInfo: WifiAwareNetworkInfo =
                        networkCapabilities.getTransportInfo() as WifiAwareNetworkInfo

                    viewModel.IpSetLaunchLocal(
                        viewModel.setIpAddressToString(peerAwareInfo.getPeerIpv6Addr()!!.address),
                        context
                    )

                    viewModel.peerPortSetLaunch(peerAwareInfo.getPort())

                    val tempPort: String = viewModel.PEER_PORT.toString()

                    val ipv6Temp: Inet6Address =
                        Inet6Address.getByAddress(viewModel.setIpStringToAddress(viewModel.IP_ADDRESS_REMOTE.toString())) as Inet6Address

                    // socket = Socket(ipv6Temp, tempPort.toInt())
                    socket = network.getSocketFactory().createSocket(ipv6Temp, tempPort.toInt())
                }

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                    //TODO: create socketServer on different thread to transfer files
                    // this program only use one Thread, valid for other usages
                    try {

                        val awareNi: NetworkInterface = NetworkInterface.getByName(
                            linkProperties.getInterfaceName()
                        )

                        val Addresses: Enumeration<InetAddress> = awareNi.getInetAddresses();

                        while (Addresses.hasMoreElements()) {
                            var addr: InetAddress = Addresses.nextElement();
                            if (addr is Inet6Address) {

                                if (addr.isLinkLocalAddress()) {

                                    viewModel.IpSetLaunchLocal(
                                        viewModel.setIpAddressToString(
                                            Inet6Address.getByAddress(
                                                "WifiAware",
                                                addr.getAddress(),
                                                awareNi
                                            ).address
                                        ), context
                                    )
                                    //    this application will not use publish
                                    //   if (publishDiscoverySession != null && peerHandle != null) {
                                    //       publishDiscoverySession.sendMessage(peerHandle, IP_ADDRESS_MESSAGE, myIP);
                                    if (subscribeDiscoverySession != null && peerHandle != null) {
                                        subscribeDiscoverySession!!.sendMessage(
                                            peerHandle!!, IP_ADDRESS_MESSAGE,
                                            viewModel.setIpStringToAddress(viewModel.getIpAddressLocalLaunch()!!)
                                        );

                                    }
                                    break;
                                }
                            }
                        }
                    } catch (e: SocketException) {
                        println("onlinkpropertychanged error socket $e")
                    } catch (e: Exception) {
                        println("onlinkpropertychanged error socket $e")
                    }
                }
            }
        )
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun discover(): Intent? {
        if (viewModel.MATCH_FILTER.toString().length<7){
        viewModel.setMatchFilterLaunch("1234567\n")}

        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)) {


            wifiAwareManager =
                context.getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager

            val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)

            val myReceiver = AwareBroadcastReceiver(context, viewModel, wifiAwareManager!!)

            return context.registerReceiver(myReceiver, filter)

        } else return null


    }

    // this function is included as reference but will not be used in this program Esp32 will be the publisher

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    fun publish(pskOrPmk: String?, isPmk: Boolean, serviceName: String, serviceInfo: String) {
        // psk is 8 bits code as string
        // pmk is 32 characters alphanumeric code
        val pmkid = ByteArray(16)
        if (pskOrPmk != null) {
            if (isPmk) {
                secConfig
                WifiAwareDataPathSecurityConfig.Builder(
                    WIFI_AWARE_CIPHER_SUITE_NCS_PK_128
                )
                    .setPmk(pskOrPmk.toByteArray())
                    .setPmkId(pmkid)
                    .build()

            } else {
                secConfig = WifiAwareDataPathSecurityConfig.Builder(
                    WIFI_AWARE_CIPHER_SUITE_NCS_PK_128
                )
                    .setPskPassphrase(pskOrPmk)
                    .build();


            }
            config = PublishConfig.Builder()
                .setServiceName(serviceName)
                .setServiceSpecificInfo(serviceInfo.toByteArray())
                .setDataPathSecurityConfig(secConfig!!)
                .setPublishType(PublishConfig.PUBLISH_TYPE_SOLICITED)
                .build()


        } else {
            config = PublishConfig.Builder()
                .setServiceName(serviceName)
                .setServiceSpecificInfo(serviceInfo.toByteArray())
                .setPublishType(PublishConfig.PUBLISH_TYPE_SOLICITED)
                .build()

        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun subscribe() {


        subscribeConfig = SubscribeConfig.Builder()
            .setServiceName(viewModel.serviceName)
            .setServiceSpecificInfo(this@WifiAware.serviceSpecificInfo)
            .setMatchFilter(this@WifiAware.matchFilter)
            .build()

        if ((wifiAwareManager!!.isDeviceAttached) and (subscribeDiscoverySession == null)) {


            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            wifiAwareSession!!.subscribe(subscribeConfig!!, object : DiscoverySessionCallback() {

                    override fun onServiceDiscovered(
                        peerHandle: PeerHandle,
                        serviceSpecificInfo: ByteArray,
                        matchFilter: List<ByteArray>
                 ) {
                      this@WifiAware.peerHandle = peerHandle

                      super.onServiceDiscovered(peerHandle, serviceSpecificInfo, matchFilter)


                        if (subscribeDiscoverySession != null) {
                            subscribeDiscoverySession!!.sendMessage(
                                peerHandle, MAC_ADDRESS_MESSAGE,
                                viewModel.setMacStringToAddress(viewModel.getMacAddressLocalLaunch()!!)
                            )
                            println("onServiceDiscovered sending mac...")
                        }

                    }


                    override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                        super.onSubscribeStarted(session);

                        subscribeDiscoverySession = session;

                        subscribeDiscoverySession!!.sendMessage(
                            peerHandle!!, MAC_ADDRESS_MESSAGE,
                            viewModel.setMacStringToAddress(viewModel.getMacAddressLocalLaunch()!!)
                        )
                        println("onServiceStarted sending mac...")

                    }


                    override fun onMessageReceived(peerHandle: PeerHandle, message: ByteArray) {
                        super.onMessageReceived(peerHandle, message)
                        if (message.size == 2) {
                            portToUse = viewModel.setPortToUse(message);
                            println("received message, port = $portToUse")

                        } else if (message.size == 6) {

                            viewModel.MacSetLaunchRemote(
                                viewModel.setMacAddressToString(message))

                        } else if (message.size == 16) {;
                            viewModel.IpSetLaunchRemote(
                                viewModel.setIpAddressToString(message))

    // this program will not use other message after this will use socket
    //                    } else if (message.size > 16) {
    //                        viewModel.receivedMessage(message);
    //                        //Toast.makeText(MainActivity.this, "message received", Toast.LENGTH_SHORT).show();
                        }
                    }
             }, null)
            }
        }

    suspend fun sendData(data: ByteArray, dataType: Int) {

            if (socket == null) {

                val tempPort: String = viewModel.PEER_PORT.toString()

                val ipv6Temp: Inet6Address =
                    Inet6Address.getByAddress(viewModel.setIpStringToAddress(viewModel.getIpAddressRemoteLaunch()!!)) as Inet6Address

                socket = Socket(ipv6Temp, tempPort.toInt())

            }

        val arrayAndType = ByteArray(data.size + 1)
        arrayAndType[0] = dataType.toByte()
        System.arraycopy(data, 0, arrayAndType, 1, data.size)

        wifiMutex.withLock {
            withContext(Dispatchers.IO) {
                flagReceived=false
                            try {
                                val inputStream: InputStream=  socket!!.getInputStream()
                                val outputStream: OutputStream = socket!!.getOutputStream()
                                val dataOutputStream: DataOutputStream =
                                    DataOutputStream(outputStream)
                                dataOutputStream.write(arrayAndType, 0, arrayAndType.size)
                                dataOutputStream.write(0xff)
                                val result= inputStream.read()
                                if (result != -1) {
                                    flagReceived = true
                                }
                                dataOutputStream.close()
                                inputStream.close()
                                }
                             catch (e: Exception) {
                                println("data could not be sent in dataOutputStream")
                            }
                        }
                    }
            }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.NEARBY_WIFI_DEVICES])
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun startWiFiAwareandSubscribe(){
        coroutineScope {
            discover()
            attachToWifi()
            subscribe()
            }
        }



    suspend fun closeSession() {
            wifiMutex.withLock {
                withContext(Dispatchers.IO) {
                    socket?.close()
                    socket = null
                }
                wifiAwareSession?.close()
                wifiAwareSession = null
                flagReceived = false
            }
        }
}


