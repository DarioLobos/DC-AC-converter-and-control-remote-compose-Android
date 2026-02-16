package com.example.dc_acconverterandcontrolremote
import kotlinx.coroutines.flow.Flow

interface DevicesRepository {

        fun getAllDevicesStream(): Flow<List<Devices>>

        fun getDeviceStream(deviceId: Int): Flow<Devices?>


        suspend fun insertDevice(device: Devices)


        suspend fun deleteDevice(device: Devices)


        suspend fun updateDevice(device: Devices)
}

