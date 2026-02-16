package com.example.dc_acconverterandcontrolremote

import kotlinx.coroutines.flow.Flow

class OfflineDevicesRepository(private val itemDao: DaoDevices) : DevicesRepository {
    override fun getAllDevicesStream(): Flow<List<Devices>> = DaoDevices.getAll()

    override fun getDeviceStream(deviceId: Int): Flow<Devices?> = DaoDevices.getItem(deviceId)

    override suspend fun insertDevice(device: Devices) = DaoDevices.insert(device)


    override suspend fun deleteDevice(device: Devices) = DaoDevices.delete(device)

    override suspend fun updateDevice(device: Devices) = Devices.update(device)
}
