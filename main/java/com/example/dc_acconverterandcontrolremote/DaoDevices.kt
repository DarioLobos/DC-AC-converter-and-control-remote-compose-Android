package com.example.dc_acconverterandcontrolremote
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

interface DaoDevices {

    @Query("SELECT * FROM devices WHERE device_number = :deviceId")
    fun getItem(deviceId: Int): Flow<Devices>


    @Query("SELECT * FROM devices WHERE device_number IN (:deviceIds)")
    fun loadAllByIds(deviceIds: IntArray): List<Devices>

    @Insert
    fun insert ( vararg device: Devices)

    @Delete
    fun delete(device: Devices)

}