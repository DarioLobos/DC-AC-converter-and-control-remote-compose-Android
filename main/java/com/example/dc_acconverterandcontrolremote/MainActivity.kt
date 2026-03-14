package com.example.dc_acconverterandcontrolremote
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.dc_acconverterandcontrolremote.ui.theme.DC_ACConverterAndControlRemoteTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    // 1. Correct ViewModel Initialization
    // This uses the Factory we built to safely get the Repo from the REAL Application instance
    private val viewModel: DeviceSchedulerViewModel by viewModels {
        DeviceSchedulerViewModel.Factory
    }

    // 2. Lateinit for WifiAware
    // We must initialize this in onCreate after the ViewModel is ready
    private lateinit var aware: WifiAware

    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Initialize WifiAware using the system-provided applicationContext
        aware = WifiAware(applicationContext, viewModel)

        enableEdgeToEdge()
        setContent {
            DC_ACConverterAndControlRemoteTheme {
                // Pass the lifecycle-managed viewModel and aware instance
                MainApp(LocalContext.current, viewModel, aware)
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        // 4. Use lifecycleScope instead of GlobalScope
        // lifecycleScope automatically cancels if the Activity is destroyed
        lifecycleScope.launch {
            aware.startWiFiAwareandSubscribe()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            aware.closeSession()
        }
    }
}
