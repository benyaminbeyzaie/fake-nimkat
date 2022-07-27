package com.nimkat.app.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.view.SnackBar
import com.nimkat.app.view.login.LoginActivity
import com.nimkat.app.view.my_questions.MyQuestionsActivity
import com.nimkat.app.view.profile_edit.CompleteProfile
import com.nimkat.app.view.profile_edit.ProfileEditActivity
import com.nimkat.app.view_model.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("RememberReturnType")
@Composable
fun Camera(
    cameraScaffoldState: ScaffoldState,
    cameraExecutor: ExecutorService,
    outputDirectory: File,
    authViewModel: AuthViewModel,
    shouldShowCamera: MutableState<Boolean>,
    onImageCaptured: (Uri, Int) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val errorSnackBar = remember { SnackbarHostState() }

    val profileModel = authViewModel.profileModelLiveData.observeAsState()
    if (profileModel.value?.status == DataStatus.ErrorWithData) {
        LaunchedEffect(lifecycleOwner.lifecycleScope) {

            errorSnackBar.showSnackbar(
                message = "متاسفانه مشکلی پیش اومده یا دستگاهت به اینترنت متصل نیست!",
                actionLabel = "RED",
                duration = SnackbarDuration.Short
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val drawerVisibility = remember {
        mutableStateOf(false)
    }

    remember(cameraScaffoldState.drawerState.isOpen) {
        drawerVisibility.value = cameraScaffoldState.drawerState.isOpen
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        drawerContent = if (drawerVisibility.value) {
            { Drawer(authViewModel = authViewModel) }
        } else null,
        scaffoldState = cameraScaffoldState,
    ) {

        Box {


            // camera
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.background))
            )
                CameraView(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = onImageCaptured,
                    onError = { Log.e("kilo", "View error:", it) },
                    errorSnackBar,
                    shouldShowCamera
                )


            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(30.dp),
                backgroundColor = colorResource(R.color.camera_title_back),
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
                        IconButton(
                            onClick = {
                                drawerVisibility.value = true
                                coroutineScope.launch {
                                    delay(100)
                                    cameraScaffoldState.drawerState.open()
                                }
                            },
                            modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Menu,
                                null,
                                tint = colorResource(R.color.white),
                            )
                        }
                    }

                    Text(
                        stringResource(R.string.take_picture),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp),
                        color = colorResource(R.color.white),
                        textAlign = TextAlign.Center,
                        fontFamily = secondFont,
                        fontSize = 20.sp
                    )
                }
            }

            SnackBar(snackbarHostState = errorSnackBar, Color.Red, true, {})

        }
    }
}

@Composable
fun Drawer(
    modifier: Modifier = Modifier, authViewModel: AuthViewModel
) {

    val context = LocalContext.current

    val authModel = authViewModel.authModelLiveData.observeAsState()
    val profileModel = authViewModel.profileModelLiveData.observeAsState()
    val isLoaded = profileModel.value?.data != null
    val isLogin = authModel.value?.data != null
    val isProfileCompleted = remember{ mutableStateOf(false)}


    Log.d("PROF", profileModel.value?.data.toString())
    if (profileModel.value?.data != null) {
        Log.d("PROF", "load status changed to loaded")
        isProfileCompleted.value = true
    } else {
        Log.d("PROF", "load status changed to unloaded")
    }

    Log.d("status" , "is loaded = " + isLoaded + " isLogin = " + isLogin + " completed = " + isProfileCompleted)
    Log.d("status2" , "status is " + authModel.value?.data.toString())
    val prefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val isDark = prefs.getBoolean(stringResource(R.string.darThemeTag), false)
    var darkTag = stringResource(id = R.string.darThemeTag)

    Column(
        modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background)),
    ) {

        if (!isLogin) {

            Image(
                painterResource(R.drawable.ic_profile),
                null,
                Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(0.dp, 24.dp, 0.dp, 0.dp)
            )

            Text(
                stringResource(R.string.drawer_title),
                fontFamily = mainFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp, 0.dp, 0.dp),
                color = colorResource(R.color.primary_text),
            )

            Spacer(modifier = Modifier.height(16.dp))

            val texts = listOf(
                R.string.drawer_desc_1,
                R.string.drawer_desc_2,
                R.string.drawer_desc_3,
                R.string.drawer_desc_4
            )

            for (i in 0 until 4) {
                Row(modifier = Modifier.padding(16.dp, 4.dp, 16.dp, 4.dp)) {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        null,
                        tint = colorResource(R.color.primary_text),
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        stringResource(texts[i]),
                        fontFamily = mainFont,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.primary_text_variant),
                    )
                }
            }

            CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
                Button(
                    onClick = {
                        LoginActivity.sendIntent(context)
                        (context as Activity).finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.blue)),
                ) {
                    Row {
                        Text(
                            text = stringResource(R.string.free_sign_in_login),
                            style = TextStyle(
                                fontFamily = secondFont
                            ),
                            color = colorResource(R.color.white),
                            fontSize = 20.sp,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1F))
        } else {

            if (isProfileCompleted.value) {
                val name1 = remember { mutableStateOf("Default") }
                val phone1 = remember { mutableStateOf("Default") }
                val grade1 = remember { mutableStateOf(profileModel.value?.data?.educationalGrade!!) }


                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(R.drawable.ic_profile),
                        null,
                        Modifier
                            .size(50.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp, 6.dp)
                    ) {
                        if (isLoaded) {
                            name1.value = profileModel.value?.data?.name!!
                            phone1.value = profileModel.value?.data?.phone!!
                            grade1.value = profileModel.value?.data?.educationalGrade!!
                        }
                        Text(
                            name1.value,
//                        "آنیتا علیخانی",
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = colorResource(R.color.primary_text),
                            textAlign = TextAlign.Right,
                            fontFamily = mainFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                phone1.value,
//                        "+989123456789",
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = colorResource(R.color.primary_text_variant),
                                textAlign = TextAlign.Right,
                                fontFamily = mainFont,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }


                    }
                    IconButton(onClick = {
                        ProfileEditActivity.sendIntent(context, name1.value, phone1.value, grade1.value)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            colorResource(R.color.primary_text)
                        )
                    }
                }
                Box(
                    Modifier
                        .padding(0.dp, 12.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                        .clickable {
                            MyQuestionsActivity.sendIntent(context)
                        }) {
                    Row(
                        Modifier
                            .padding(24.dp, 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.my_questions),
                            modifier = Modifier
                                .weight(1f),
                            color = colorResource(R.color.blue),
                            textAlign = TextAlign.Right,
                            fontFamily = mainFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_my_questions),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.blue)
                        )
                    }
                }
                Box(
                    Modifier
                        .padding(0.dp, 4.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                        .clickable {
                            val browserIntent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://t.me/benyamin_beyzaie")
                                )
                            startActivity(context, browserIntent, null)
                        }) {
                    Row(
                        Modifier
                            .padding(24.dp, 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.contact_us),
                            modifier = Modifier
                                .weight(1f),
                            color = colorResource(R.color.blue),
                            textAlign = TextAlign.Right,
                            fontFamily = mainFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.blue)
                        )
                    }
                }
            }else{
                Column(
                    Modifier
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(id = R.string.drawer_title_incomplete_profile),
                        color = colorResource(R.color.primary_text),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    Text(
                        stringResource(id = R.string.drawer_desc_incomplete_profile),
                        color = colorResource(R.color.primary_text_variant),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontSize = 15.sp
                    )
                    Button(
                        onClick = {
                            CompleteProfile.sendIntent(context)
                            (context as Activity).finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.blue)),
                    ) {
                        Row {
                            Text(
                                text = stringResource(R.string.profile_completion),
                                style = TextStyle(
                                    fontFamily = secondFont
                                ),
                                color = colorResource(R.color.white),
                                fontSize = 20.sp,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1F))
            Box(
                Modifier
                    .padding(0.dp, 4.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .clickable {
                        authViewModel.clearAuth()
                    }) {
                Row(
                    Modifier
                        .padding(24.dp, 5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.logout),
                        modifier = Modifier
                            .weight(1f),
                        color = colorResource(R.color.red),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colorResource(R.color.red)
                    )
                }
            }

            Box(
                Modifier
                    .padding(0.dp, 4.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .clickable {
                        authViewModel.delete()
                        authViewModel.clearAuth()
                    }) {
                Row(
                    Modifier
                        .padding(24.dp, 5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "حذف اکانت",
                        modifier = Modifier
                            .weight(1f),
                        color = colorResource(R.color.red),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colorResource(R.color.red)
                    )
                }
            }
        }

        val darkState = remember { mutableStateOf(isDark) }


        Row(
            modifier = Modifier.padding(20.dp, 5.dp, 20.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.background_color),
                color = colorResource(R.color.blue),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(0.1F))
            Image(
                painterResource(R.drawable.ic_day),
                null,
                Modifier.size(20.dp)
            )
            Switch(
                checked = darkState.value,
                onCheckedChange = {
                    darkState.value = it
                    prefs.edit()
                        .putBoolean(darkTag, darkState.value)
                        .apply()
                    if (darkState.value) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                },
                enabled = true,
                modifier = Modifier.padding(6.dp, 0.dp, 6.dp, 0.dp)
            )
            Image(
                painterResource(R.drawable.ic_night),
                null,
                Modifier.size(20.dp)
            )
        }


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
            Log.e("kilo", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            onImageCaptured(savedUri, 0)
        }
    })


}
