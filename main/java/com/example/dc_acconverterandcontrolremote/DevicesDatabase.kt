package com.example.dc_acconverterandcontrolremote
import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.room.Room


@Database(entities = arrayOf(Devices::class), version = 1, exportSchema = false)
abstract class DevicesDatabase : RoomDatabase() {
    abstract fun daoDevices(): DaoDevices

    companion object {
        @Volatile
        private var Instance: DevicesDatabase? = null

        fun DevicesDataBase (): DevicesDatabase {
            var context : Context?=null

            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext , DevicesDataBase::class.java, "com.example.dc_acconverterandcontrolremote")
                    .build()
                    .also { Instance = it }

            }
        }
    }
}