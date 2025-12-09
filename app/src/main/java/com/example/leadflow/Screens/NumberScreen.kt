package com.example.leadflow.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.leadflow.R
import com.example.leadflow.ui.theme.BorderGray
import com.example.leadflow.ui.theme.InputBg
import com.example.leadflow.ui.theme.ItemGrayBg
import com.example.leadflow.ui.theme.ItemSelectedBg
import com.example.leadflow.ui.theme.PrimaryColor
import com.example.leadflow.ui.theme.TextDark
import com.example.leadflow.ui.theme.TextGray
import kotlinx.coroutines.launch


// Data model for a phone number
data class PhoneNumberModel(
    val id: Int,
    val number: String,
    val flagEmoji: String = "🇺🇸" // Default US Flag emoji
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberScreen(onBackClick: () -> Unit, onHomeClick: () -> Unit){


    var searchText by remember { mutableStateOf("") }

    var selectedIndex by remember { mutableIntStateOf(2) } // Index 2 is selected in the image

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val scope = rememberCoroutineScope()

    // Sample Data
    val phoneNumbers = remember {
        listOf(
            PhoneNumberModel(1, "(517) 684-3476"),
            PhoneNumberModel(2, "(517) 486-4337"),
            PhoneNumberModel(3, "(517) 654-5475"),
            PhoneNumberModel(4, "(517) 587-8954"),
            PhoneNumberModel(5, "(517) 879-9875"),
            PhoneNumberModel(6, "(517) 514-8547"),
            PhoneNumberModel(7, "(517) 814-4153"),
        )
    }

    Scaffold(
        containerColor = Color.White,

        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = PrimaryColor,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            // --- 1. Custom Back Button (Grey Circle) ---
            Box(
                modifier = Modifier
                    .size(35.dp) // Circle ka size
                    .clip(CircleShape) // Bilkul Gol
                    .background(Color(0xFFF7F8F9)) // Light Grey Background (InputBg jaisa)
                    .border(1.dp, Color(0xFFE8ECF4), CircleShape) // Optional: Halki si border
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 2. Heading Text (Ab Back Arrow ke neeche hai) ---
            Text(
                text = "Choose your number",
                fontSize = 24.sp, // Thora bara font header ke liye
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search here", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = InputBg,
                    focusedContainerColor = InputBg,
                    unfocusedBorderColor = BorderGray,
                    focusedBorderColor = PrimaryColor,
                    cursorColor = PrimaryColor
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // List of Phone Numbers
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Padding for FAB
            ) {
                itemsIndexed(phoneNumbers) { index, item ->
                    NumberItemCard(
                        phoneNumber = item,
                        isSelected = index == selectedIndex,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
        // --- NEW COMPONENT: The Bottom Sheet ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White, // Sheet ka background white
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                // Sheet Content goes here
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 40.dp), // Bottom padding for buttons
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Lottie Animation Container
                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // IMPORTANT: Replace R.raw.lottie_completed with your actual file name
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(R.raw.success) // <-- Apni file lagao yahan
                        )
                        val progress by animateLottieCompositionAsState(
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Title Text
                    Text(
                        text = "Scan Completed",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Description Text
                    Text(
                        text = "The message has been sent successfully.\nPlease proceed via the website.",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // 4. Button 1: Scan More (Blue Filled)
                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Scan More",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Button 2: Go to Home (Light Blue Background)
                    Button(
                        onClick = {
                            scope.launch {
                                // Pehle sheet ko hide karo (Animate down)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                // Jab sheet poori band ho jaye, tab ye code chalega
                                showBottomSheet = false
                                onHomeClick() // Ab navigate karo
                            }
                                  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        // Custom colors for light background and blue text
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBlueBg,
                            contentColor = PrimaryColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Go to Home",
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
fun NumberItemCard(
    phoneNumber: PhoneNumberModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Determine colors based on selection state
    val backgroundColor = if (isSelected) ItemSelectedBg else ItemGrayBg
    val borderColor = if (isSelected) PrimaryColor else Color.Transparent
    val textColor = if (isSelected) PrimaryColor else TextGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            // Clip shapes the background
            .clip(RoundedCornerShape(12.dp))
            // Background color changes based on selection
            .background(backgroundColor)
            // Border appears only if selected
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Flag Emoji
        Text(
            text = phoneNumber.flagEmoji,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 2. Phone Number Text
        Text(
            text = phoneNumber.number,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )

        // Push following elements to the end
        Spacer(modifier = Modifier.weight(1f))

        // 3. Checkmark Icon (only if selected)
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = PrimaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


