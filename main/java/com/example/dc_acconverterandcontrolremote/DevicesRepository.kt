package com.example.dc_acconverterandcontrolremote
import kotlinx.coroutines.flow.Flow

interface DevicesRepository {

    /**
     * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
     */
    interface DevicesRepository {
        /**
         * Retrieve all the items from the the given data source.
         */
        fun getAllDevicesStream(): Flow<Devices>

        /**
         * Retrieve an item from the given data source that matches with the [id].
         */
        fun getDeviceStream(deviceId: Int): Flow<Devices?>

        /**
         * Insert item in the data source
         */
        suspend fun insertDevice(device: Devices)

        /**
         * Delete item from the data source
         */
        suspend fun deleteDevice(device: Devices)

        /**
         * Update item in the data source
         */
        suspend fun updateDevice(device: Devices)
    }

}