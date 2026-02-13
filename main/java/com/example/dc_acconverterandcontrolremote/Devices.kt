package com.example.dc_acconverterandcontrolremote
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
class Devices (
    @PrimaryKey(autoGenerate = true)
    val device_number: Int = 0,
    val device_name: String ="Device 0"

)