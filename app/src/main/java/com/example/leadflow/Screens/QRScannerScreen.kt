package com.example.leadflow.Screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

import java.util.concurrent.Executors

@Composable
fun QRScannerScreen(onQrScanned: (String) -> Unit,
                    onPermissionDenied: () -> Unit) {
    val context  = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    )
    }

    //Permission Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted

            // 2. LOGIC YAHAN HAI: Agar permission nahi mili to kya karein?
            if (!granted) {
                Toast.makeText(context, "Permission Denied: Camera needed for scanning", Toast.LENGTH_SHORT).show()
                onPermissionDenied() // Wapis Home bhejo
            }
        }
    )

    //Camera permission when Screen Start
    LaunchedEffect(Unit) {
        if(!hasCameraPermission){
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if(hasCameraPermission){
        Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
            // Camera Preview View
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        // 1. Preview Setup
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        // 2. Image Analyzer Setup (QR Code padhne ke liye)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1920, 1080))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(
                            Executors.newSingleThreadExecutor(),
                            QrCodeAnalyzer { url ->
                                // Jab URL mil jaye to wapis bhejo
                                onQrScanned(url)
                            }
                        )

                        // 3. Camera Bind karo (Back Camera)
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues())
            )
            ScannerOverlay()

            // 3. BORDER BOX (Visual White Border beech mein)

        }
    }
}

// --- Helper Class: Jo actually image ko process karegi ---
class QrCodeAnalyzer(
    private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Is flag se hum rokenge ke ek hi code bar bar scan na ho
    private var isScanning = true

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && isScanning) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // QR Code Format Options
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { url ->
                            // Agar URL mil gaya to scanning roko aur result bhejo
                            isScanning = false
                            onQrDetected(url)
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close() //Image close karna zaroori hai
                }
        } else {
            imageProxy.close()
        }
    }
}

@Composable
fun ScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val boxSize = 250.dp.toPx() // Box ka size pixels mein
        val left = (canvasWidth - boxSize) / 2
        val top = (canvasHeight - boxSize) / 2

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // 1. Poori screen Black (Dim) karo
            drawRect(Color.Black.copy(alpha = 0.5f))

            // 2. Beech mein se color hata do (Transparent Hole)
            drawRoundRect(
                topLeft = Offset(left, top),
                // YAHAN CHANGE KIYA HAI:
                // 'ComposeSize' ki jagah poora path likh diya hai taake confusion na ho
                size = androidx.compose.ui.geometry.Size(boxSize, boxSize),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                color = Color.Transparent,
                blendMode = BlendMode.Clear
            )

            restoreToCount(checkPoint)
        }
    }
}