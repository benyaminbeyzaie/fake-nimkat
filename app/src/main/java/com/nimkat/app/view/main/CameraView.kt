package com.nimkat.app.view.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nimkat.app.R
import com.nimkat.app.view.SnackBar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri, Int) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    snackbarHostState: SnackbarHostState,
    shouldShowCamera: MutableState<Boolean>
) {
    // 1
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()


    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }


    val canTakePicture = remember{ shouldShowCamera}

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("permission", "Permission granted")
            canTakePicture.value = true
        } else {
            Log.d("permission", "Permission denied")
        }
    }


    fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("permission", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                (context as Activity),
                Manifest.permission.CAMERA
            ) -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    // 3
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize())


        FloatingActionButton(
            onClick = {
                if (canTakePicture.value) {
                    takePhoto(
                        imageCapture = imageCapture,
                        outputDirectory = outputDirectory,
                        executor = executor,
                        onImageCaptured = onImageCaptured,
                        onError = onError,
                    )
                }else{
                    requestCameraPermission()
                }
            },
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(24.dp)
                .size(80.dp),
            backgroundColor = colorResource(R.color.blue)
        ) {
            Icon(
                painterResource(R.drawable.ic_camera),
                null,
                tint = colorResource(R.color.white),
            )
        }

        FloatingActionButton(
            onClick = {
                pickFromGallery(
                    onImageCaptured = onImageCaptured,
                )

            },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(24.dp)
                .size(50.dp),
            backgroundColor = colorResource(R.color.blue)
        ) {
            Icon(
                Icons.Outlined.Collections,
                null,
                tint = colorResource(R.color.white),
            )
        }
        SnackBar(snackbarHostState = snackbarHostState, Color.Red, true) {}

    }
}


private fun pickFromGallery(
    onImageCaptured: (Uri, Int) -> Unit,
) {
    onImageCaptured(Uri.parse(""), 1)
}


private fun takePhoto(
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri, Int) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            Log.e("ImageCapture", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            onImageCaptured(savedUri, 0)
        }
    })


}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }
