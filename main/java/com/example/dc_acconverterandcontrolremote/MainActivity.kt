package com.example.dc_acconverterandcontrolremote
import com.example.dc_acconverterandcontrolremote.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dc_acconverterandcontrolremote.ui.theme.DC_ACConverterAndControlRemoteTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // For lists of items
import androidx.compose.material3.*
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.nio.file.WatchEvent
import androidx.constraintlayout.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import androidx.room.Room
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

lateinit var devices: List <Devices>
lateinit var devicesDao: DaoDevices

private val Context.myDataStore by preferencesDataStore(name = "settings")

val MAC_ADDRESS = intPreferencesKey("mac_adrress")
val IP_ADDRESS = intPreferencesKey("ip_adrress")
val NUMBER_DEVICES = intPreferencesKey("number_devices")

const val default_nbr_devices:Int = 8

fun devicesDataBase (context: Context) {


    val db = Room.databaseBuilder(
        context, DevicesDatabase::class.java, "DEVICES"
    ).build()
    devicesDao = db.daoDevices()
}

suspend fun devicesSet(context: Context, nbr_devices:Int) {
    context.myDataStore.edit {
        it[NUMBER_DEVICES]=nbr_devices
    }
    //pending toast
}

suspend fun macAddressSet(context: Context, mac_address:Int) {
    context.myDataStore.edit {
        it[MAC_ADDRESS]=mac_address
    }
    //pending toast
}

suspend fun IPAddressSet(context: Context, ip_address:Int) {
    context.myDataStore.edit {
        it[IP_ADDRESS]=ip_address
    }
    //pending toast
}

fun devicesInit(context: Context) {

    devicesSet(
        context,
        default_nbr_devices)
}

fun deviceListInit() {
    for (i in 0..default_nbr_devices){
    devicesDao.insert(Devices(i,"Devices $i"))
}

fun deviceList():List<Devices> {
    return devicesDao.getAll()
}

fun deviceName(device_nbr:Int ):String {
    val device: List<Devices> = devicesDao.getItem(device_nbr) as List<Devices>
    val name: String = device.get(0).device_name
    return name
}

suspend fun deviceListSizeUpdate(context: Context, qty_devices: Int) {

    val devices: Int = context.myDataStore.data.map{
        it[NUMBER_DEVICES]?:0 }.toString().toInt()

    if (devices>qty_devices){

        for(i in (devices-1) downTo (qty_devices-1)){
        devicesDao.delete(i)
    }
        //pending toast


    devicesSet(context,qty_devices)

}

fun sendActionToWiFI(device_number: Int, on_or_off: String){
    // pending
}

@Composable
fun ButtonstoONOFF(device_number : Int, on_or_off : String, modifier: Modifier){

    Button  (onClick = {
                sendActionToWiFI(device_number, on_or_off)
            },
            colors = { ButtonColors(Color.Cyan, contentColor = Color.Blue) } ,
            content = {Text(on_or_off,color=Color.Black, fontSize = 20.sp)},
            modifier = modifier.wrapContentSize() )

}

@Composable
fun constraionWithButtonsOnOff(device_number:Int, device_name:String) {


    ConstraintLayout(Modifier
        .wrapContentSize()
        .background(color = Color.Cyan)) {

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
                linkTo(buttonOff.start)
            }
        val modifierOff: Modifier = Modifier
            .constrainAs(buttonOff) {
                top.linkTo(titleName.bottom, margin = 5.dp)
                bottom.linkTo(parent.bottom, margin = 5.dp)
                linkTo(buttonOn.end)
            }

        Text(text = device_name, color = Color.Black, modifier = modifierText)
        ButtonstoONOFF(device_number, on, modifierOn)
        ButtonstoONOFF(device_number, off, modifierOff)
        createHorizontalChain(
            buttonOn, buttonOff,
            chainStyle = ChainStyle.SpreadInside
        )
    }

}

@Composable
fun LazyGridForButtonsMain(){


        LazyVerticalGrid(columns = GridCells.Fixed(2),Modifier
            .wrapContentSize()
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(deviceList().size){
            constraionWithButtonsOnOff(it, deviceName(it) )
            }
        }
    }

}

@Composable
fun DropMenuSettings(){
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier= Modifier
            .padding(16.dp)
    ){
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.action_settings) )
        }
        DropdownMenu(
            expanded = expanded, onDismissRequest = {expanded = false}
        ) {
            DropdownMenuItem(
                text= {Text(stringResource(R.string.actions_eettings))},
                onClick = {

                }
            )
        }
    }


}

class MainActivity : ComponentActivity() {

    val context: Context = applicationContext
    devicesInit(context)
    devicesDataBase(context)
    devicesListInit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DC_ACConverterAndControlRemoteTheme {
            }
        }
        val context: Context = applicationContext


    }
}

}

@Preview(showBackground = true)
