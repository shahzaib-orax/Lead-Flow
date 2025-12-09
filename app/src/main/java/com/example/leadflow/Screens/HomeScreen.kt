package com.example.leadflow.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.leadflow.R
import com.example.leadflow.ui.theme.BorderColor
import com.example.leadflow.ui.theme.PrimaryColor
import kotlin.Unit


val LightBlueBg = Color(0xFFDDF7FF)     // Very Light Blue for Card Background
val TextColorDark = Color(0xFF1E232C)
val TextColorGray = Color(0xFF8391A1)

@Composable
fun HomeScreen(onScanClick: () -> Unit) {
    Scaffold(

        bottomBar = { BottomNavBar() },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Logo at Top
            Image(

                painter = painterResource(id = R.drawable.splash_icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(180.dp)
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))

            // 2. Main Card (Light Blue Background)
            Card(
                colors = CardDefaults.cardColors(containerColor = LightBlueBg),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // --- White Container for Lottie ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .padding(8.dp), // Thori padding box ke andar
                        contentAlignment = Alignment.Center
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(R.raw.qr_scan_anim)
                        )
                        val progress by animateLottieCompositionAsState(
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            // 1. fillMaxSize poora use karega (koi 0.9f nahi)
                            modifier = Modifier.fillMaxSize(),
                            // 2. Crop use karne se animation 'Zoom' ho jayegi aur box fill karegi
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Text: Fast & Easy
                    Text(
                        text = "Fast & Easy QR Scan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColorDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Text: Subtitle
                    Text(
                        text = "Scan codes smoothly with one tap",
                        fontSize = 18.sp,
                        color = TextColorGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Button: Scan QR Code
                    Button(
                        onClick = { onScanClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Scan QR Code",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun BottomNavBar() {

    var selectedItem by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.background(Color.White)
    ) {
        HorizontalDivider(
            thickness = 2.dp,
            color = BorderColor
        )
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 1.dp,
            modifier = Modifier.padding(bottom = 1.dp)
        ) {
            // Home Item
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = selectedItem == 0,
                onClick = {selectedItem = 0 },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColor,
                    selectedTextColor = PrimaryColor,
                    indicatorColor = LightBlueBg
                )
            )
            // Settings Item
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Setting") },
                label = { Text("Setting") },
                selected = selectedItem == 1,
                onClick = { selectedItem = 1},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColor,
                    selectedTextColor = PrimaryColor,
                    indicatorColor = LightBlueBg
                )
            )
        }
    }
}

