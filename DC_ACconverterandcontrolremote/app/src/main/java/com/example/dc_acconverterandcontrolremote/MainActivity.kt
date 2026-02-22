package com.example.dc_acconverterandcontrolremote
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
//import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dc_acconverterandcontrolremote.DevicesDatabase.Companion.DevicesDataBase
import com.example.dc_acconverterandcontrolremote.ui.theme.DC_ACConverterAndControlRemoteTheme


class MainActivity : ComponentActivity() {

   // private val model: DeviceSchedulerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
         setContent {

            DC_ACConverterAndControlRemoteTheme {
                DatabaseApplication()


                val modelComposeM3= viewModel<DeviceSchedulerViewModel>()
                modelComposeM3.devicesDao= DevicesDataBase(this).daoDevices()
                MainApp(this,modelComposeM3 )


            }
        }

    }
}



