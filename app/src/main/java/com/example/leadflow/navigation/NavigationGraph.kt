package com.example.leadflow.navigation

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatapp.navigation.Screen
import com.example.leadflow.Screens.HomeScreen
import com.example.leadflow.Screens.NumberScreen
import com.example.leadflow.Screens.QRScannerScreen
import com.example.leadflow.Screens.SignInScreen
import com.example.leadflow.network.RetrofitInstance
import com.example.leadflow.network.TwilioConfig
import kotlinx.coroutines.launch

@Composable
fun NavigationGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                })
        }

        composable(Screen.Home.route) {
            HomeScreen(onScanClick = {
                navController.navigate(Screen.QrScannerScreen.route)
            })
        }

        composable(Screen.NumberScreen.route) {
            NumberScreen(
                onBackClick = {
                    navController.navigate(Screen.Home.route)
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        composable(Screen.QrScannerScreen.route) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            // 1. State banayi UI ko control karne ke liye
            var isProcessing by remember { mutableStateOf(false) }

            // 2. Box use kiya taake Overlay dikha sakein
            Box(modifier = Modifier.fillMaxSize()) {

                QRScannerScreen(
                    onQrScanned = { scannedUrl ->
                        // Agar pehle se process ho raha hai to dobara mat chalo
                        if (!isProcessing) {
                            isProcessing = true // UI show karo

                            // Vibration Feedback
                            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if (Build.VERSION.SDK_INT >= 26) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                                )
                            } else {
                                vibrator.vibrate(100)
                            }

                            // 1 Second Toast
                            val toast = Toast.makeText(context, "QR Scanning...", Toast.LENGTH_SHORT)
                            toast.show()
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                toast.cancel()
                            }, 1000)

                            // API Call Logic
                            scope.launch {
                                try {
                                    // --- STEP 1: Apne Server se Number/Text mangwaya ---
                                    val response = RetrofitInstance.api.sendScannedUrl(scannedUrl)

                                    if (response.isSuccessful) {
                                        val smsData = response.body()

                                        if (smsData != null && smsData.success) {
                                            Log.d("TWILIO", "Data Received -> Number: ${smsData.number}, Msg: ${smsData.text}")
                                            Toast.makeText(context, "Data Received. Sending SMS...", Toast.LENGTH_SHORT).show()

                                            // --- STEP 2: AB TWILIO KO BHEJENGE ---
                                            try {
                                                // Config file se data utha kar API call ki
                                                val twilioResponse = RetrofitInstance.api.sendSmsViaTwilio(
                                                    url = TwilioConfig.BASE_URL,
                                                    authHeader = TwilioConfig.getAuthHelper(),
                                                    to = smsData.number,
                                                    from = TwilioConfig.SENDER_NUMBER,
                                                    body = smsData.text
                                                )

                                                if (twilioResponse.isSuccessful) {
                                                    Log.d("TWILIO", "Success: SMS Sent!")
                                                    Toast.makeText(context, "SMS Sent Successfully!", Toast.LENGTH_LONG).show()

                                                    // Success ke baad Navigate karo
                                                    navController.navigate(Screen.NumberScreen.route)

                                                } else {
                                                    Log.e("TWILIO", "Failed: ${twilioResponse.code()} - Check Keys")
                                                    Toast.makeText(context, "Twilio Error (Fake Keys)", Toast.LENGTH_LONG).show()
                                                    isProcessing = false
                                                    navController.navigate(Screen.NumberScreen.route)
                                                }

                                            } catch (e: Exception) {
                                                Log.e("TWILIO_EXCEPTION", e.toString())
                                                Toast.makeText(context, "Twilio Connection Failed", Toast.LENGTH_SHORT).show()
                                                isProcessing = false
                                            }
                                            // --- TWILIO END ---

                                        } else {
                                            Toast.makeText(context, "Server Error: No Data", Toast.LENGTH_SHORT).show()
                                            isProcessing = false
                                        }
                                    } else {
                                        Toast.makeText(context, "API Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                        isProcessing = false
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Internet Error", Toast.LENGTH_SHORT).show()
                                    isProcessing = false
                                    navController.navigate(Screen.Home.route)
                                }
                            }
                        }
                    },
                    onPermissionDenied = {
                        navController.navigate(Screen.Home.route)
                    }
                )

                // 3. --- SCAN COMPLETE UI (Overlay) ---
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable(enabled = false) {}, // Clicks block kiye
                        contentAlignment = Alignment.Center
                    ) {
                        // White Card wapis lagaya taake UI clean lage
                        Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Blue Rounded Indicator (Loader)
                                CircularProgressIndicator(
                                    modifier = Modifier.size(60.dp),
//                                    color = Color(0xFF0095C8), // FIXED COLOR HEX
                                    color = Color(0xFFEF0254), // FIXED COLOR HEX
                                    strokeWidth = 5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
