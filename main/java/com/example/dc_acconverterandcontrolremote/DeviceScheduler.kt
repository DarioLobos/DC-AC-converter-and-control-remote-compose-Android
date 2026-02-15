package com.example.dc_acconverterandcontrolremote
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.*
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import java.util.*
import androidx.compose.ui.platform.LocalContext

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditTextONOFF(context: Context, device_number: Int, on_or_off: String, modifier: Modifier) {

        var hourSet: Int? = 12
        var minuteSet: Int? = 0
        var timeToShow: String = stringResource(R.string.click)
        var selectedTime: TimePickerState? by remember { mutableStateOf(null) }
        var showTimePicker by remember { mutableStateOf(true) }
        val calendar = Calendar.getInstance()
        val hourForPicker: Int = hourSet ?: calendar[Calendar.HOUR_OF_DAY]
        val minuteForPicker: Int = minuteSet ?: calendar[Calendar.MINUTE]
        val device: List<Devices> = devicesDao?.getItem(device_number) as List<Devices>
        val string_onoff: String = stringResource(R.string.ON)


        if (on_or_off == string_onoff) {
            hourSet = device.get(0).hour_on
            minuteSet = device.get(0).minutes_on
            timeToShow = "$hourSet : $minuteSet"
        } else {
            hourSet = device.get(0).hour_off
            minuteSet = device.get(0).minutes_off
            timeToShow = "$hourSet : $minuteSet"
        }

        val timePickerState = rememberTimePickerState(
            initialHour = hourForPicker,
            initialMinute = minuteForPicker,
            is24Hour = true
        )

        Box(propagateMinConstraints = false) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {

                Button(
                    onClick = { showTimePicker = true }

                ) {
                    Text(timeToShow)
                }

                if (showTimePicker) {

                    TimePicker(
                        state = timePickerState,
                        Modifier.fillMaxSize()
                    )
                    Button(

                        onClick = {
                            showTimePicker = false
                            Toast.makeText(context, R.string.setTimeCancel, Toast.LENGTH_SHORT).show()
                        })
                    {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            selectedTime = timePickerState
                            showTimePicker = false
                            Toast.makeText(context, R.string.setTimeCancel, Toast.LENGTH_SHORT).show()
                        }) {
                        Text(stringResource(R.string.confirm))

                        if (selectedTime != null) {

                            if (on_or_off == string_onoff) {
                                device.get(0).hour_on = selectedTime!!.hour
                                device.get(0).minutes_on = selectedTime!!.minute
                            } else {
                                device.get(0).hour_off = hourSet
                                device.get(0).minutes_off = minuteSet
                            }
                            timeToShow = "$hourSet : $minuteSet"
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun ConstrainWithEditTextOnOff(context: Context, device_number: Int, device_name: String) {

        ConstraintLayout(
            Modifier
                .wrapContentSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            val on: String = stringResource(R.string.ON)
            val off: String = stringResource(R.string.OFF)
            val (editOn, editOff, titleName, timeOn, timeOff, daysOfWeek) = createRefs()

            val modifierText: Modifier = Modifier
                .constrainAs(titleName) {
                    top.linkTo(parent.top, margin = 10.dp)
                    bottom.linkTo(editOff.top, margin = 10.dp)
                    centerHorizontallyTo(parent)
                }

            val modifierTextON: Modifier = Modifier
                .constrainAs(timeOn) {
                    top.linkTo(titleName.bottom, margin = 5.dp)
                    bottom.linkTo(daysOfWeek.top, margin = 5.dp)
                    end.linkTo(editOn.start)
                }

            val modifierEditOn: Modifier = Modifier
                .constrainAs(editOn) {
                    top.linkTo(titleName.bottom, margin = 5.dp)
                    bottom.linkTo(daysOfWeek.top, margin = 5.dp)
                    start.linkTo(timeOn.end)
                    end.linkTo(editOff.start)
                }

            val modifierTextOFF: Modifier = Modifier
                .constrainAs(timeOff) {
                    top.linkTo(titleName.bottom, margin = 5.dp)
                    bottom.linkTo(daysOfWeek.top, margin = 5.dp)
                    start.linkTo(editOn.end)
                    end.linkTo(editOff.start)
                }

            val modifierEditOff: Modifier = Modifier
                .constrainAs(editOff) {
                    top.linkTo(titleName.bottom, margin = 5.dp)
                    bottom.linkTo(daysOfWeek.top, margin = 5.dp)
                    start.linkTo(timeOff.end)
                }


            val modifierRButtonDaysOfWeek = Modifier
                .wrapContentSize()
                .constrainAs(daysOfWeek) {
                    top.linkTo(editOn.bottom, margin = 5.dp)
                    bottom.linkTo(parent.bottom, margin = 5.dp)
                    centerHorizontallyTo(parent)
                }


            Text(
                text = device_name,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = modifierText
            )
            Text(
                text = stringResource(R.string.timeOn),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = modifierTextON
            )
            Text(
                text = stringResource(R.string.timeOFF),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = modifierTextOFF
            )
            EditTextONOFF(context, device_number, on, modifierEditOn)
            EditTextONOFF(context, device_number, off, modifierEditOff)
            createHorizontalChain(
                editOn, editOff,
                chainStyle = ChainStyle.SpreadInside
            )

            var days_Selected: Int = 0

            val selectedOptions =
                remember { mutableStateListOf(false, false, false, false, false, false, false) }

            val options = listOf(
                stringResource(R.string.Sun), stringResource(R.string.Mon),
                stringResource(R.string.Tue), stringResource(R.string.Wed),
                stringResource(R.string.Thu), stringResource(R.string.Fri),
                stringResource(R.string.Sat)
            )

            MultiChoiceSegmentedButtonRow(modifier = modifierRButtonDaysOfWeek) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        checked = selectedOptions[index],
                        onCheckedChange = {
                            selectedOptions[index] = !selectedOptions[index]
                        },
                        label = {
                            when (label) {
                                options[0] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 1
                                    } else {
                                        days_Selected = (days_Selected and (1).inv())
                                    }

                                options[1] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 2
                                    } else {
                                        days_Selected = (days_Selected and (2).inv())
                                    }

                                options[2] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 4
                                    } else {
                                        days_Selected = (days_Selected and (4).inv())
                                    }

                                options[3] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 8
                                    } else {
                                        days_Selected = (days_Selected and (8).inv())
                                    }

                                options[4] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 16
                                    } else {
                                        days_Selected = (days_Selected and (16).inv())
                                    }

                                options[5] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 32
                                    } else {
                                        days_Selected = (days_Selected and (32).inv())
                                    }

                                options[6] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 64
                                    } else {
                                        days_Selected = (days_Selected and (64).inv())
                                    }

                                options[7] ->
                                    if (selectedOptions[index] == true) {
                                        days_Selected = days_Selected or 128
                                    } else {
                                        days_Selected = (days_Selected and (128).inv())
                                    }

                            }

                            val device: List<Devices> =
                                devicesDao?.getItem(device_number) as List<Devices>
                            device.get(0).days_week = days_Selected

                        }
                    )
                }
            }

        }


    }


    @Composable
    fun DeviceScheduler_Screen(context: Context) {

        val devicesListSize: Int = deviceList()?.size ?: 0

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), Modifier
                .wrapContentSize()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(devicesListSize) {
                ConstrainWithEditTextOnOff(context, it, deviceName(it) ?: "")
            }
        }
    }


