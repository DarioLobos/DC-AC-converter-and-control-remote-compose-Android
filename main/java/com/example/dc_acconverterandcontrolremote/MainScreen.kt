package com.example.dc_acconverterandcontrolremote
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.map
import androidx.constraintlayout.compose.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.example.dc_acconverterandcontrolremote.DevicesDatabase.Companion.DevicesDataBase

var devices: List <Devices>?= null

private val Context.myDataStore by preferencesDataStore(name = "settings")

val MAC_ADDRESS = intPreferencesKey("mac_address")
val IP_ADDRESS = intPreferencesKey("ip_address")
val NUMBER_DEVICES = intPreferencesKey("number_deices")

const val default_nbr_devices:Int = 8

var devicesDao: DaoDevices= DevicesDataBase().daoDevices()

override val devicesRepository: DevicesRepository by lazy {
    OfflineDevicesRepository(DevicesDatabase.DevicesDataBase().daoDevices())
}


@Composable
suspend fun DevicesSet(nbr_devices:Int) {
    LocalContext.current.myDataStore.edit {
        it[NUMBER_DEVICES]=nbr_devices
    }
    Toast.makeText(LocalContext.current,R.string.toast_set_nbr_devices, Toast.LENGTH_SHORT)
}

@Composable
suspend fun macAddressSet(mac_address:Int) {
    LocalContext.current.myDataStore.edit {
        it[MAC_ADDRESS]=mac_address
    }
    Toast.makeText(LocalContext.current,R.string.toast_set_MAC, Toast.LENGTH_SHORT).show()
}

@Composable
suspend fun IPAddressSet(ip_address:Int) {
    LocalContext.current.myDataStore.edit {
        it[IP_ADDRESS]=ip_address
    }
    Toast.makeText(context,R.string.toast_set_IP, Toast.LENGTH_SHORT).show()
}

suspend fun devicesInit(context: Context) {

    devicesSet(default_nbr_devices)
}

suspend fun deviceListInit() {
    for (i in 0..default_nbr_devices) {
        devicesDao?.insert(Devices(i, "Devices $i"))
    }
}

    fun deviceList():List<Devices>? {
        if (devicesDao != null) {
            return devicesDao?.getAll()
        }
        else return null
    }

    fun deviceName(device_nbr:Int ):String? {
        if (devicesDao != null) {
            val device: List<Devices> = devicesDao?.getItem(device_nbr) as List<Devices>
            val name: String = device.get(0).device_name
            return name
        } else {
            return null
        }
    }


    suspend fun deviceListSizeUpdate(context: Context, qty_devices: Int) {
        val devices: Int = context.myDataStore.data.map {
            it[NUMBER_DEVICES] ?: 0
        }.toString().toInt()

        if (devices > qty_devices) {

            for (i in (devices - 1) downTo (qty_devices - 1)) {
                devicesDao?.delete((devicesDao?.getItem(i) as List<Devices>).get(0))
            }

            DevicesSet(qty_devices)
            val deleted = devices - qty_devices
            val toast_message: String = "$deleted " + Resources.getSystem().getString((R.string.devices_added_toast))
            Toast.makeText(context, toast_message, Toast.LENGTH_SHORT).show()

        } else if (devices < qty_devices) {

            for (i in devices..(qty_devices - 1)) {
                devicesDao?.insert(Devices(i, "Devices $i"))
            }
            val added = qty_devices - devices
            val toast_message: String = "$added " + Resources.getSystem().getString((R.string.devices_added_toast))
            Toast.makeText(context, toast_message, Toast.LENGTH_SHORT).show()


        }
    }

fun sendActionToWiFI(device_number: Int, on_or_off: String){
    // pending
}


@Composable
fun ButtonstoONOFF(device_number : Int, on_or_off : String, modifier: Modifier) {

    ElevatedButton(
        onClick = {
        sendActionToWiFI(device_number, on_or_off)
        },
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        content = { Text(on_or_off, color = Color.Black, fontSize = 20.sp) },
        modifier = modifier.wrapContentSize()
    )

    }

@Composable
fun ConstraionWithButtonsOnOff(device_number:Int, device_name:String) {

    ConstraintLayout(Modifier
        .wrapContentSize()
        .background(color = MaterialTheme.colorScheme.background)) {

        val on: String = stringResource(R.string.ON)
        val off: String = stringResource(R.string.OFF)
        val (buttonOn, buttonOff, titleName) = createRefs()

        val modifierText: Modifier = Modifier
            .constrainAs(titleName) {
                top.linkTo(parent.top, margin = 10.dp)
                bottom.linkTo(buttonOff.top, margin = 10.dp)
                centerHorizontallyTo(parent)
            }


        val modifierOn: Modifier = Modifier
            .constrainAs(buttonOn) {
                top.linkTo(titleName.bottom, margin = 5.dp)
                bottom.linkTo(parent.bottom, margin = 5.dp)
                end.linkTo(buttonOff.start)
            }
        val modifierOff: Modifier = Modifier
            .constrainAs(buttonOff) {
                top.linkTo(titleName.bottom, margin = 5.dp)
                bottom.linkTo(parent.bottom, margin = 5.dp)
                start.linkTo(buttonOn.end)
            }

        Text(text = device_name,
            color= MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = modifierText)
        ButtonstoONOFF(device_number, on, modifierOn)
        ButtonstoONOFF(device_number, off, modifierOff)
        createHorizontalChain(
            buttonOn, buttonOff,
            chainStyle = ChainStyle.SpreadInside
        )
    }

}
@Preview(name = "lazy grid")
@Composable
fun LazyGridForButtonsMain(){

        val devicesListSize: Int = deviceList()?.size ?: 0

        LazyVerticalGrid(columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(devicesListSize){
            ConstraionWithButtonsOnOff(it, deviceName(it)?:"" )
            }
        }
    }
@Preview(name = "MainScreen")
@Composable
fun MainScreen (){
    LazyGridForButtonsMain()
}