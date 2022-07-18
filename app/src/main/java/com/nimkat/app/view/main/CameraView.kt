package com.nimkat.app.view.main

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.nimkat.app.R
import com.nimkat.app.view.question_crop.QuestionCropActivity
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
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


    val launchCropImage =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Toast.makeText(context, "Image Cropped", Toast.LENGTH_SHORT).show()

        }


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

    // 3
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        FloatingActionButton(
            onClick = {
                Log.i("kilo", "ON CLICK")
                takePhoto(
                    context = context,
                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    onImageCaptured = onImageCaptured,
                    onError = onError,
                )

                val testImagePath =
                    Environment.getExternalStorageDirectory().path.plus("/test.png")

                Log.d("kilouri", testImagePath)

                launchCropImage.launch(
                    QuestionCropActivity.sendIntent(context as Activity, testImagePath)
                )

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

    }
}


private fun takePhoto(
    context: Context,
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {


//    val testImagePath =
//        Environment.getExternalStorageDirectory().path.plus("/test.png")
//    val photoFile = File(testImagePath)

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

//    val photoFile = File(
//        Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DCIM
//        ), "Camera"
//    )


//    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())
//    val name = "test"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Nimkat")
        }
    }


    // Create output options object which contains file + metadata
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("kilo", "Take photo error:", exception)
//                Toast.makeText(context, "Image ERROR", Toast.LENGTH_SHORT).show()
                onError(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
//                Toast.makeText(context, "Image SAVED", Toast.LENGTH_SHORT).show()
                savedUri.toString().substring(8)
                Log.d("kilouri", savedUri.toString().substring(7))
                onImageCaptured(savedUri)

            }
        })
//

//    var bitmap: Bitmap? = null
//    imageCapture.takePicture(executor, object :
//        ImageCapture.OnImageCapturedCallback() {
//        override fun onCaptureSuccess(image: ImageProxy) {
//            //get bitmap from image
//            bitmap = imageProxyToBitmap(image)
////            super.onCaptureSuccess(image)
//        }
//
//        override fun onError(exception: ImageCaptureException) {
//                Log.e("kilo", "Take photo error:", exception)
////                Toast.makeText(context, "Image ERROR", Toast.LENGTH_SHORT).show()
//                onError(exception)
//
//            }
//
//    })
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val planeProxy = image.planes[0]
    val buffer: ByteBuffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }