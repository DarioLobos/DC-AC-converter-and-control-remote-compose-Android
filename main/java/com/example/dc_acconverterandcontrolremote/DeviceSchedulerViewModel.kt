package com.example.dc_acconverterandcontrolremote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData;
import java.util.Calendar

class DeviceSchedulerViewModel: ViewModel() {

    val device_number: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }


    val on_or_off: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    val device: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val hourSet: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val minuteSet: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    var calendar by mutableStateOf(Calendar.getInstance())

    var hourForPicker by mutableStateOf(calendar[Calendar.HOUR_OF_DAY])

    var minuteForPicker by mutableStateOf(calendar[Calendar.MINUTE])

    val days_Selected: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val selectedTime: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun deviceName(device_number: Int): String{
        val device: List<Devices> = devicesDao.getItem(device_number) as List<Devices>

        return   device.get(0).device_name
    }

    fun seton_or_off(on_or_off: String){
        this.on_or_off.setValue(on_or_off)
    }
    fun setdevice_number(device_number: Int){
        this.device_number.setValue(device_number)
    }

    fun setselectedTime(
        hourToSeT: Int,
        minuteToSet: Int,
        device_number: Int,
        on_or_off: String,
        string_onoff: String
    ): String {

        val device: List<Devices> = devicesDao.getItem(device_number) as List<Devices>

        if ((hourToSeT >= 0) and ((minuteToSet >= 0))) {

            if (on_or_off == string_onoff) {
                device.get(0).hour_on = hourToSeT
                device.get(0).minutes_on = minuteToSet
            } else {
                device.get(0).hour_off = hourToSeT
                device.get(0).minutes_off = minuteToSet
            }
        }
        return "$hourSet : $minuteSet"
    }

    fun setDeviceNumber(device_number:Int){
        this.device_number.setValue(device_number)
    }

    fun setOnorOff(on_or_off: String){
        this.on_or_off.setValue(on_or_off)
    }


    fun TimesToshow(device_number: Int, on_or_off: String, string_onoff: String): String {

        val device: List<Devices> = devicesDao.getItem(device_number) as List<Devices>
        var timeToShow: String = ""

        if (on_or_off == string_onoff) {
            hourSet.setValue(device.get(0).hour_on!!)
            minuteSet.setValue(device.get(0).minutes_on!!)
            timeToShow = "$hourSet : $minuteSet"
        } else {
            hourSet.setValue(device.get(0).hour_off!!)
            minuteSet.setValue(device.get(0).minutes_off!!)
            timeToShow = "$hourSet : $minuteSet"
        }
        return timeToShow
    }

    fun setselectedTime(
        hourToSeT: Int,
        minuteToSet: Int,
        device_number: Int,
        on_or_off: String,
        string_onoff: String
    ): String {
        val device: List<Devices> = devicesDao.getItem(device_number) as List<Devices>

        if ((hourToSeT >= 0) and ((minuteToSet >= 0))) {

            if (on_or_off == string_onoff) {
                device.get(0).hour_on = hourToSeT
                device.get(0).minutes_on = minuteToSet
            } else {
                device.get(0).hour_off = hourToSeT
                device.get(0).minutes_off = minuteToSet
            }
        }
        return "$hourSet : $minuteSet"
    }


    fun selectedDays(daysselected: Int, option: Int, selectedOptions: Boolean) {
        var temp: Int = daysselected
        when (option) {
            0 ->
                if (selectedOptions == true) {
                    temp = daysselected or 1
                    days_Selected.setValue(temp)
                } else {
                    temp = daysselected and (1).inv()
                    days_Selected.setValue(temp)
                }

            1 ->
                if (selectedOptions == true) {
                    temp = daysselected or 2
                    days_Selected.setValue(temp)
                } else {
                    temp = daysselected and (2).inv()
                    days_Selected.setValue(temp)
                }

            2 ->
                if (selectedOptions == true) {
                    temp = daysselected or 4
                    days_Selected.setValue(temp)
                } else {
                    temp = daysselected and (4).inv()
                    days_Selected.setValue(temp)
                }

            3 ->
                if (selectedOptions == true) {
                    temp = daysselected or 8
                    days_Selected.setValue(temp)
                } else {
                    temp = daysselected and (8).inv()
                    days_Selected.setValue(temp)
                }

            4 ->
                if (selectedOptions == true) {
                    temp = daysselected or 16
                    days_Selected.setValue(temp)
                } else {
                    temp = daysselected and (16).inv()
                    days_Selected.setValue(temp)
                }

            5 ->
                if (selectedOptions == true) {
                    temp = daysselected or 32
                    days_Selected.setValue(temp)
                } else {
                    temp = daysselected and (32).inv()
                    days_Selected.setValue(temp)
                }

            6 ->
                if (selectedOptions == true) {
                    temp = daysselected or 64
                    days_Selected.setValue(temp)
                } else {
                    temp = daysselected and (64).inv()
                    days_Selected.setValue(temp)
                }

        }
    }
}