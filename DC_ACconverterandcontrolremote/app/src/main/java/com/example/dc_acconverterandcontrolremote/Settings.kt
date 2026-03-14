package com.example.dc_acconverterandcontrolremote
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextToAddressError (addressText: String, isError: Boolean ) {
    if (isError) {
        if (addressText.isEmpty()) Text(stringResource(R.string.required))
        else Text(stringResource(R.string.more12))

    }
}
@Composable
fun TextToDeviceError (deviceText: String, isError: Boolean) {
    if (isError) {
        if (deviceText.isEmpty()) Text(stringResource(R.string.required))
        else Text(stringResource(R.string.more3))
    }
}

@Composable
fun Settings_Screen(model: DeviceSchedulerViewModel, context: Context){

    var ipAddressText by remember { mutableStateOf(  model.IP_ADDRESS_REMOTE.toString())}
    var macAddressText by remember { mutableStateOf( model.MAC_ADDRESS_REMOTE.toString())}
    var matchFilterText by remember { mutableStateOf( model.MATCH_FILTER.toString())}
    var numberDevicesText by remember { mutableStateOf( model.NUMBER_DEVICES.toString())}
    var isBlurredIp by remember {   mutableStateOf(false)}
    var isBlurredMac by remember {  mutableStateOf(false)}
    var isBlurredDev by  remember { mutableStateOf(false)}
    val isErrorMac: Boolean =  ( macAddressText.length!=12) or ( macAddressText.isEmpty())
    val isErrorIp: Boolean =  ( ipAddressText.length!=12) or ( ipAddressText.isEmpty())
    val isErrorDev: Boolean =  ( numberDevicesText.toInt()>100) or ( numberDevicesText.isEmpty())
    val isErrorMat: Boolean =  ( matchFilterText.length!=7) or ( matchFilterText.isEmpty())

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
                         model.IpSetLaunchLocal(ipAddressText,context )
                         isBlurredMac = false
                     }},
                 value = ipAddressText,
                 onValueChange = { newText: String ->
                     if (newText.all { it.isDigit() }) {
                         if (newText.length==12){
                         ipAddressText = newText}
                     }
                     if (!isBlurredMac) isBlurredMac = true
                 },
                 isError = isErrorIp,
                 supportingText = {TextToAddressError(ipAddressText, isErrorIp)},
                 enabled = true,
                 readOnly = false,
                 label = { Text (stringResource(R.string.ipAddress))},
                 singleLine = true,
                 visualTransformation = VisualTransformation { text ->
                     var out = ""
                     for (i in text.indices) {
                         out += text[i]
                         if (i % 3 == 2 && i < 12) out += "."
                     }
                     TransformedText(
                         text = AnnotatedString(out),
                         offsetMapping = object : OffsetMapping {
                             override fun originalToTransformed(offset: Int): Int {
                                 if (offset < 3) return offset
                                 if (offset < 6) return offset + 1
                                 if (offset < 9) return offset + 2
                                 return 15
                             }

                             override fun transformedToOriginal(offset: Int): Int {
                                 if (offset < 4) return offset
                                 if (offset < 7) return offset - 1
                                 if (offset < 10) return offset - 2
                                 return 12
                             }

                         }

                     )
                 }

             )
         }

        Row(modifier = Modifier
            .wrapContentSize())
        {


            
            OutlinedTextField(
                modifier = Modifier.onFocusChanged { focusState ->
                    if (!focusState.isFocused && isBlurredIp) {
                        model.MacSetLaunchLocal(macAddressText,context )
                        isBlurredIp = false
                    }},
                value = macAddressText,
                onValueChange = { newText: String ->
                    if (newText.all { it.isDigit() or (it in 'a'..'f') or (it in 'A'..'F') }) {
                        macAddressText = newText
                    }

                    if (!isBlurredMac) isBlurredMac = true
                },
                isError = isErrorMac,
                supportingText = { TextToAddressError(macAddressText, isErrorMac)},
                enabled = true,
                readOnly = false,
                label = {Text (stringResource(R.string.macAddress))},
                singleLine = true,
                visualTransformation = VisualTransformation { text ->
                    var out = ""
                    for (i in text.indices) {
                        out += text[i]
                        if (i % 2 == 1 && i < 12) out += "."
                    }
                    TransformedText(
                        text = AnnotatedString(out),
                        offsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int {
                                if (offset < 2) return offset
                                if (offset < 4) return offset + 1
                                if (offset < 6) return offset + 2
                                if (offset < 8) return offset + 3
                                if (offset < 10) return offset + 4
                                return 17
                            }

                            override fun transformedToOriginal(offset: Int): Int {
                                if (offset < 3) return offset
                                if (offset < 5) return offset - 1
                                if (offset < 7) return offset - 2
                                if (offset < 9) return offset - 3
                                if (offset < 11) return offset - 4
                                return 12
                            }

                        }

                    )
                }
            )
        }

            Row(modifier = Modifier
                .wrapContentSize())
            {



                OutlinedTextField(
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (!focusState.isFocused && isBlurredIp) {
                            model.setMatchFilterLaunch(matchFilterText)
                            isBlurredIp = false
                        }},
                    value = matchFilterText,
                    isError = isErrorMat,
                    onValueChange = { newText: String ->
                        if (newText.all { it.isDigit()} and (newText.length>7)) {
                            matchFilterText = newText
                        }

                        if (!isBlurredMac) isBlurredMac = true
                    },
                    label = {Text (stringResource(R.string.matchFilter))},
                    singleLine = true)
        }

        Row(modifier = Modifier
            .wrapContentSize())
        {
            OutlinedTextField(
                modifier = Modifier.onFocusChanged { focusState ->
                    if (!focusState.isFocused && isBlurredDev) {
                        model.numberSetLaunch(numberDevicesText)
                        isBlurredIp = false
                    }},
                value = numberDevicesText,
                onValueChange = { newText: String ->
                    if (newText.all { it.isDigit()  }) {
                        if (newText.toInt()< 100){
                        numberDevicesText = newText
                    }
                    }

                    if (!isBlurredDev) isBlurredDev = true
                },
                isError = isErrorDev,
                supportingText = {TextToDeviceError(numberDevicesText, isErrorDev)},
                enabled = true,
                readOnly = false,
                label = {Text (stringResource(R.string.ipAddress))},
                singleLine = true
                            )

        }
    }
        }

}