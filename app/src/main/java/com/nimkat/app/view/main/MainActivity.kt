package com.nimkat.app.view.main

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.ETC1.encodeImage
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.FirebaseApp
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.utils.ASK_FOR_EDIT_PROFILE
import com.nimkat.app.utils.CROP_IMAGE_CODE
import com.nimkat.app.utils.toast
import com.nimkat.app.view.login.LoginActivity
import com.nimkat.app.view.question_crop.QuestionCropActivity
import com.nimkat.app.view.search.QuestionSearchActivity
import com.nimkat.app.view_model.AskQuestionViewModel
import com.nimkat.app.view_model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        fun sendIntent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                context.startActivity(this)
            }
    }


    private var cameraScaffoldState: ScaffoldState? = null
    private var coroutineScope: CoroutineScope? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private val authViewModel: AuthViewModel by viewModels()
    private val askQuestionsViewModel: AskQuestionViewModel by viewModels()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        authViewModel.initAuth()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        var flag = false;

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDark = prefs.getBoolean(getString(R.string.darThemeTag), false)

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        requestCameraPermission()

        askQuestionsViewModel.discoveryAnswers.observe(this) { value ->
            when (value?.status) {
                DataStatus.NeedLogin -> {
                    LoginActivity.sendIntent(this)
                }
                DataStatus.Success -> {
                     value.data?.let { list ->
                        if (flag) QuestionSearchActivity.sendIntent(
                            this,
                            list,
                            askQuestionsViewModel.questionId.value!!.data.toString()
                        )
                    }
                }
                DataStatus.Error -> {
                    this.toast("Error : ".plus(value.message.toString()))
                }
                else -> {}
            }
        }
        flag = true;


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
                        authViewModel,
                        cameraExecutor,
                        outputDirectory,
                        onImageCaptured = ::handleImageCapture,
                        shouldShowCamera = shouldShowCamera,
                        askQuestionViewModel = askQuestionsViewModel,
                    )
                }
            }
        }
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
        finish()
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


    private fun handleImageCapture(uri: Uri, mode: Int) {
        val intent = Intent(this@MainActivity, QuestionCropActivity::class.java)
        intent.putExtra("mode", mode)
        intent.putExtra("URI", uri)
        startActivityForResult(intent, CROP_IMAGE_CODE)
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val x = this
        when (requestCode) {
            CROP_IMAGE_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.apply {
                        val parcelableExtra = getParcelableExtra<Uri>("photouri")
                        Toast.makeText(x, "IMAGE CROPPING SUCCESSFULL.", Toast.LENGTH_SHORT).show()
                        // now we should use this uri to load bitmap of the image and then send it to server
                        val viewModel: AskQuestionViewModel by viewModels()
                        val imageUri: Uri = parcelableExtra!!
                        val imageStream: InputStream? = contentResolver.openInputStream(imageUri)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        val encodedImage: String = encodeImage(selectedImage)
                        viewModel.askImageQuestion(encodedImage)
                    }
                } else {
                    Toast.makeText(this, "IMAGE CROPPING CANCELED.", Toast.LENGTH_SHORT).show()
                }
            }
            ASK_FOR_EDIT_PROFILE -> {
                if (resultCode == RESULT_OK) {
                    data?.apply {
                        val grade = getStringExtra("grade")
                        val gradeID = getIntExtra("gradeID", 0)
                        val name = getStringExtra("name")
                        authViewModel.update(name!!, gradeID)
                        Toast.makeText(x, "profile changed .", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }


}


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun Greeting(
    cameraScaffoldState: ScaffoldState,
    authViewModel: AuthViewModel,
    cameraExecutor: ExecutorService,
    outputDirectory: File,
    shouldShowCamera: MutableState<Boolean>,
    onImageCaptured: (Uri, Int) -> Unit,
    askQuestionViewModel: AskQuestionViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current




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
                                TextQuestion(askQuestionViewModel)
                            }
                            1 -> {
                                Camera(
                                    cameraScaffoldState,
                                    cameraExecutor,
                                    outputDirectory,
                                    authViewModel,
                                    shouldShowCamera,
                                    onImageCaptured = onImageCaptured
                                )
                            }
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
