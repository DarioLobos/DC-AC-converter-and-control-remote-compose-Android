package com.example.dc_acconverterandcontrolremote
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.isDigitsOnly

@Composable
fun Settings_Screen(model: DeviceSchedulerViewModel, context: Context){
    var IpAddressText = rememberTextFieldState(initialText = model.IP_ADDRESS.toString()?:"")
    var MacAddressText = rememberTextFieldState(initialText = model.MAC_ADDRESS.toString()?:"")
    var numberDevicesText = rememberTextFieldState(initialText = model.NUMBER_DEVICES.toString()?:"")
    var isBlurredIp by remember { mutableStateOf(false) }
    var isBlurredMac by remember { mutableStateOf(false) }
    var isBlurredDev by remember { mutableStateOf(false) }
    val isErrorIP = IpAddressText.toString().isEmpty() || IpAddressText.toString().length >= 12

    Box(propagateMinConstraints = false) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(modifier = Modifier
                .wrapContentSize())
         {
             OutlinedTextField(
                 modifier = Modifier.onFocusChanged { focusState ->
                     if (!focusState.isFocused && isBlurredMac) {
                         model.MacSetLaunch(MacAddressText.toString(),context )
                         isBlurredMac = false
                     }},
                 value = MacAddressText,
                 onValueChange = { newText ->
                     if (newText.all { newText.isDigit() }) {
                         if (newText.toString().length==12)
                         MacAddressText = newText
                     }

                     if (isBlurredMac==false) isBlurredMac = true
                 },
                 isError = isErrorMac,
                 supportingText = {
                     if (isErrorMac) {
                         Text(if (MacAddressText.toString().isEmpty()) stringResource(R.string.required) else stringResource(R.string.more12))
                     }
                 },
                 enabled = true,
                 readOnly = false,
                 label = Text (stringResource(R.string.ipAddress)),
                 singleLine = true
             )
         }

        Row(modifier = Modifier
            .wrapContentSize())
        {


            
            OutlinedTextField(
                modifier = Modifier.onFocusChanged { focusState ->
                    if (!focusState.isFocused && isBlurredIp) {
                        model.MacSetLaunch(MacAddressText.toString(),context )
                        isBlurredIp = false
                    }},
                value = MacAddressText,
                onValueChange = { newText ->
                    if (newText.all { newText.isDigit() }) {
                        MacAddressText = newText
                    }

                    if (isBlurredIp==false) isBlurredIp = true
                },
                isError = isErrorIP,
                supportingText = {
                    if (isErrorIP) {
                        Text(if (IpAddressText.toString().isEmpty()) stringResource(R.string.required) else stringResource(R.string.more12))
                    }
                },
                enabled = true,
                readOnly = false,
                label = Text (stringResource(R.string.ipAddress)),
                singleLine = true
            )
        }
        Row(modifier = Modifier
            .wrapContentSize())
        {

            OutlinedTextField(
                value = numberDevicesText,
                onValueChange = {model.numberSetLaunch(numberDevicesText.toString(),context )},
                enabled = true,
                readOnly = false,
                label = Text (stringResource(R.string.ipAddress)),
                placeholder =  Text(stringResource(R.string.click)) ,
                singleLine = true
            )
        }
    }
        }

}