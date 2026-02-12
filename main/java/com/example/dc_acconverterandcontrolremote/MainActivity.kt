package com.example.dc_acconverterandcontrolremote

import android.R
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
import androidx.room.Room
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.map

lateinit var devices: List <Devices>
lateinit var devicesDao: DaoDevices

private val Context.myDataStore by preferencesDataStore(name = "settings")


val MAC_ADDRESS = intPreferencesKey("mac_adrress")
val IP_ADDRESS = intPreferencesKey("mac_adrress")
val NUMBER_DEVICES = intPreferencesKey("mac_adrress")

fun devicesDataBase (context: Context) {
    val db = Room.databaseBuilder(
        context, DevicesDatabase::class.java, "DEVICES"
    ).build()
    devicesDao = db.daoDevices()
}

fun devicesInit() {

}



fun sendActionToWiFI(devinice_number: Int, on_or_off: String){
    // pending
}

@Composable
fun ButtonstoONOFF(device_number : Int, on_or_off : String, modifier: Modifier){

    Button  (onClick = {
                sendActionToWiFI(device_number, on_or_off)
            },
            colors = { ButtonColors(Color.Cyan, contentColor = Color.Blue)} ,
            content = {Text(on_or_off,color=Color.Black, fontSize = 20.sp)},
            modifier = modifier.wrapContentSize(), shape =     )

}

@Composable
fun constraionWithButtonsOnOff(device_number:Int, device_name:String) {


    ConstraintLayout(Modifier
        .wrapContentSize()
        .background(color = Color.Cyan)) {

        val on: String = "ON"
        val off: String = "OFF"
        val (buttonOn, buttonOff, titleName) = createRefs()
        val modifierOn: Modifier = Modifier
            .constrainAs(buttonOn) {
                top.linkTo(parent.top, margin = 10.dp)
                bottom.linkTo(parent.bottom, margin = 10.dp)
                linkTo(buttonOff.start)
            }
        val modifierOff: Modifier = Modifier
            .constrainAs(buttonOff) {
                top.linkTo(parent.top, margin = 5.dp)
                bottom.linkTo(parent.bottom, margin = 5.dp)
                linkTo(buttonOn.end)
            }

        Text(text = device_name, color = Color.Black, modifier = Modifier.constrainAs(titleName))

        ButtonstoONOFF(device_number, on, modifierOn)
        ButtonstoONOFF(device_number, off, modifierOff)
        createHorizontalChain(
            buttonOn, buttonOff,
            chainStyle = ChainStyle.SpreadInside
        )
    }

}

@Composable
fun LazyGridForButtonsMain(quantityofdevices:Int){


        LazyVerticalGrid(columns = GridCells.Fixed(2),Modifier
            .wrapContentSize()
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(itemlist){
            constraionWithButtonsOnOff(it, mutableLisfNames(it) )
            }
        }
    }

}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DC_ACConverterAndControlRemoteTheme {
            }
        }
    }
}

}

@Preview(showBackground = true)
