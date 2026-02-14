package com.example.dc_acconverterandcontrolremote
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dc_acconverterandcontrolremote.ui.theme.DC_ACConverterAndControlRemoteTheme
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DC_ACConverterAndControlRemoteTheme {
            }
        }
        val context: Context = applicationContext


        lifecycleScope.launch {
            // repeatOnLifecycle will run the block when the lifecycle is CREATED or above
            // and cancel it when the lifecycle goes below CREATED (i.e., DESTROYED)
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                if (devicesDao == null) {
                    devicesInit(context)
                    devicesDataBase(context)
                    deviceListInit()
                }
            }

        }

        setContent {
            DC_ACConverterAndControlRemoteTheme() {

                MainApp()

            }
        }

    }
}



