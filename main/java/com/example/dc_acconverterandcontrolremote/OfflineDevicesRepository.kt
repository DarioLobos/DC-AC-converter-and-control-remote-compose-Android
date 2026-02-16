package com.example.dc_acconverterandcontrolremote
import kotlinx.coroutines.flow.Flow

class OfflineDevicesRepository(private val itemDao: DaoDevices) : DevicesRepository {
    override fun getAllItemsStream(): Flow<List<Devices>> = DaoDevices.getAll()

    override fun getItemStream(id: Int): Flow<Devices?> = DaoDevices.getItem(id)

    override suspend fun insertItem(item: Devices) = itemDao.insert(item)

    override suspend fun deleteItem(item: Devices) = itemDao.delete(item)

    override suspend fun updateItem(item: Devices) = itemDao.update(item)
}
