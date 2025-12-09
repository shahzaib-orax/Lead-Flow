package com.example.leadflow

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.window.SplashScreen
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
import androidx.core.os.postDelayed
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.leadflow.navigation.NavigationGraph

import com.example.leadflow.ui.theme.LeadFlowTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler



class MainActivity : ComponentActivity() {

    private var KeepSplashOnScreen = true
    private val delayTime = 2000L
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { KeepSplashOnScreen }

        // 4. Handler ki jagah Coroutine use kiya (Best Practice)
        lifecycleScope.launch {
            delay(delayTime)
            KeepSplashOnScreen = false
        }





        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            LeadFlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavigationGraph(navController =  navController)
                }
            }
        }
    }
}

