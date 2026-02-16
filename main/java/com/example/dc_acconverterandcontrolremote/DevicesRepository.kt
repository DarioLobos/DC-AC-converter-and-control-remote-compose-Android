package com.example.dc_acconverterandcontrolremote
import kotlinx.coroutines.flow.Flow

interface DevicesRepository {

    /**
     * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
     */
    interface ItemsRepository {
        /**
         * Retrieve all the items from the the given data source.
         */
        fun getAllItemsStream(): Flow<List<Devices>>

        /**
         * Retrieve an item from the given data source that matches with the [id].
         */
        fun getItemStream(id: Int): Flow<Devices?>

        /**
         * Insert item in the data source
         */
        suspend fun insertItem(item: Devices)

        /**
         * Delete item from the data source
         */
        suspend fun deleteItem(item: Devices)

        /**
         * Update item in the data source
         */
        suspend fun updateItem(item: Devices)
    }

}