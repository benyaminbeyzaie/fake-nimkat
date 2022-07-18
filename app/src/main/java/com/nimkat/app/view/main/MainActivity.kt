package com.nimkat.app.view.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.utils.CROP_IMAGE_CODE
import com.nimkat.app.utils.MOBILE
import com.nimkat.app.view.otp.OtpActivity
import com.nimkat.app.view.question_crop.QuestionCropActivity
import com.nimkat.app.view_model.AuthViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var cameraScaffoldState: ScaffoldState? = null
    private var coroutineScope: CoroutineScope? = null

    companion object {
        fun sendIntent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                context.startActivity(this)
            }
    }

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel: AuthViewModel by viewModels()
        authViewModel.initAuth()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(R.color.background)
                ) {
                    cameraScaffoldState = rememberScaffoldState()
                    coroutineScope = rememberCoroutineScope()
                    Greeting(
                        cameraScaffoldState!!,
                        viewModel(),
                        cameraExecutor,
                        outputDirectory,
                        onImageCaptured = ::handleImageCapture
                    )
                }
            }
        }
        requestCameraPermission()

    }

    override fun onBackPressed() {
        cameraScaffoldState?.let {
            if (it.drawerState.isOpen) {
                coroutineScope?.launch {
                    it.drawerState.close()
                }
                return
            }
        }

        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("permission", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.d("permission", "Permission denied")
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("permission", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.d("permission", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    private fun handleImageCapture(uri: Uri , mode: Int) {
        Log.i("kilo", "Image captured: $uri")
//        shouldShowCamera.value = false
        val intent = Intent(this@MainActivity, QuestionCropActivity::class.java)
        intent.putExtra("mode" , mode)
        if (uri != null){
            intent.putExtra("URI", uri)
        }
        startActivityForResult(intent, CROP_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val x = this
        when (requestCode) {
            CROP_IMAGE_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.apply {
                        val parcelableExtra = getParcelableExtra<Uri>("photouri")
                        Log.d("kiloURI", "IMAGE CROPPING SUCCESSFULL. $parcelableExtra")
                        Toast.makeText(x, "IMAGE CROPPING SUCCESSFULL.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(x, MainActivity2::class.java)
                        intent.putExtra("URI", parcelableExtra)
                        startActivity(intent)
                    }


                } else {
                    Log.d("kiloURI", "IMAGE CROPPING CANCELED.")
                    Toast.makeText(this, "IMAGE CROPPING CANCELED.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Greeting(
    cameraScaffoldState: ScaffoldState,
    authViewModel: AuthViewModel,
    cameraExecutor: ExecutorService,
    outputDirectory: File,
    onImageCaptured: (Uri , Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = 2,
        infiniteLoop = false
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column {
            Card(
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                modifier = Modifier
                    .weight(1f)
                    .background(color = colorResource(R.color.main_color))
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { index ->
                        when (index) {
                            0 -> {
                                TextQuestion()
                            }
                            1 -> {
                                Camera(
                                    cameraScaffoldState,
                                    cameraExecutor,
                                    outputDirectory,
                                    authViewModel,
                                    onImageCaptured = onImageCaptured
                                )
                            }
//                            2 -> {
//                                Gallery()
//                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .background(colorResource(R.color.main_color))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BnvItem(
                    0,
                    R.string.text_question,
                    pagerState,
                    Modifier
                        .weight(1.0F)
                        .fillMaxHeight()
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        })
                BnvItem(
                    1,
                    R.string.camera,
                    pagerState,
                    Modifier
                        .weight(1.0F)
                        .fillMaxHeight()
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        })
//                BnvItem(
//                    2,
//                    R.string.gallery,
//                    pagerState,
//                    Modifier
//                        .weight(1.0F)
//                        .fillMaxHeight()
//                        .clickable {
//                            coroutineScope.launch {
//                                pagerState.animateScrollToPage(2)
//                            }
//                        })
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun BnvItem(index: Int, @StringRes titleRes: Int, pagerState: PagerState, modifier: Modifier) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(titleRes),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = if (index == pagerState.currentPage) colorResource(R.color.white) else colorResource(
                R.color.bnv_unselected_color
            ),
            fontSize = 13.sp
        )
        AnimatedVisibility(
            visible = index == pagerState.currentPage,
        ) {
            Divider(
                color = colorResource(id = R.color.white),
                thickness = 3.dp,
                modifier = Modifier
                    .width(30.dp)
                    .padding(0.dp, 4.dp, 0.dp, 0.dp)
            )
        }
    }
}
