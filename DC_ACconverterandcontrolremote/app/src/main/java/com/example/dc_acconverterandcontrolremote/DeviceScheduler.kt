package com.example.dc_acconverterandcontrolremote
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.ui.res.stringResource
import com.example.dc_acconverterandcontrolremote.R
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
//import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModel
import kotlin.Int

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun editTextONOFF(context: Context, device_number: Int,
                       on_or_off:Boolen, modifier: Modifier,
                      viewModel : DeviceSchedulerViewModel){

        val hourForPicker: MutableLiveData<Int> by lazy {
            MutableLiveData<Int>()
        }

        val minuteForPicker: MutableLiveData<Int> by lazy {
            MutableLiveData<Int>()
        }
        var calendar by mutableStateOf(Calendar.getInstance())

        hourForPicker.set(calendar[Calendar.HOUR_OF_DAY])

        minuteForPicker.set(calendar[Calendar.MINUTE])

        val string_onoff: String = if (on_or_off) stringResource(R.string.ON) else stringResource(R.string.OFF)

        var timeToShow by remember { mutableStateOf("") }
        timeToShow= viewModel.TimesToshow(device_number, on_or_off)?:""

        var showTimePicker by remember { mutableStateOf(false) }

        val timePickerState = rememberTimePickerState(
            initialHour = hourForPicker,
            initialMinute = minuteForPicker,
            is24Hour = true
        )

        Box(propagateMinConstraints = false) {
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                showTimePicker = false

                TextField(
                    readOnly = false,
                    enabled = false,
                    value = timeToShow,
                    onValueChange = { /* ... */ },
                    label =  string_onoff ,
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.click)) },
                    modifier = modifier.clickable {

                        hourForPicker.set(calendar[Calendar.HOUR_OF_DAY])

                        minuteForPicker.set(calendar[Calendar.MINUTE])

                        showTimePicker = true
                    }
                )


                if (showTimePicker) {

                    TimePicker(
                        state = timePickerState,

                        Modifier.fillMaxSize()
                    )
                    Button(

                        onClick = {
                            showTimePicker = false
                            Toast.makeText(context, R.string.setTimeCancel, Toast.LENGTH_SHORT)
                                .show()
                        })
                    {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {

                            viewModel.setselectedTime(
                                timePickerState.hour, timePickerState.minute,
                                device_number, on_or_off)
                            timeToShow= viewModel.TimesToshow(device_number, on_or_off)
                            showTimePicker = false
                            Toast.makeText(context, R.string.setTimeRecorded, Toast.LENGTH_SHORT)
                                .show()
                        }) {
                        Text(stringResource(R.string.confirm))


                    }
                }
            }
        }
    }


@Composable
fun DeviceControlCard( context: Context, device_number: Int,
                       on_or_off:Boolen,
                       viewModel : DeviceSchedulerViewModel ) {


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
                text = viewModel.deviceName(device_number),
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

            EditTextONOFF( context, device_number, true, modifierEditOn, viewModel)

            EditTextONOFF( context, device_number, false, modifierEditOFF, viewModel)

            createHorizontalChain(
                editOn, editOff,
                chainStyle = ChainStyle.SpreadInside
            )

            val options = listOf(
                stringResource(R.string.Sun), stringResource(R.string.Mon),
                stringResource(R.string.Tue), stringResource(R.string.Wed),
                stringResource(R.string.Thu), stringResource(R.string.Fri),
                stringResource(R.string.Sat)
            )

            val selectedOptions =
                remember { mutableStateListOf(false, false, false, false, false, false, false) }

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
                            viewModel.selectedDays(device_number, index, selectedOptions[index])
                        },
                        label = { options[index] }

                    )
                }
            }

        }


    }




    @Composable
    fun deviceScheduler_Screen( context: Context, device_number: Int,
                                on_or_off:Boolen,
                                viewModel : DeviceSchedulerViewModel ) {

        val isReady by model.isInitialized.collectAsState()

        val devicesListSize: Int = model.deviceList()?.size ?: 0

        if (!isReady) {
            // Center the loader
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), Modifier
                    .wrapContentSize()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(devicesListSize) {
                        ConstrainWithEditTextOnOff(
                            context, device_number,
                            on_or_off,
                            viewModel )

                }
            }
        }
    }