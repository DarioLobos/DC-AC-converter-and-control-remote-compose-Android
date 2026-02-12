package com.example.dc_acconverterandcontrolremote
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Devices::class), version = 1)
abstract class DevicesDatabase : RoomDatabase() {
        abstract fun daoDevices(): DaoDevices
    }