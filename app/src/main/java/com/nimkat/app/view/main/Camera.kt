package com.nimkat.app.view.main

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimkat.app.R
import com.nimkat.app.view.login.LoginActivity
import com.nimkat.app.view.my_questions.MyQuestionsActivity
import com.nimkat.app.view.profile_edit.ProfileEditActivity
import com.nimkat.app.view.question_crop.QuestionCropActivity
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("RememberReturnType")
@Composable
fun Camera(cameraScaffoldState: ScaffoldState) {

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
            { Drawer() }
        } else null,
        scaffoldState = cameraScaffoldState,
    ) {

        Box {

            // camera
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
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

            val context = LocalContext.current
            CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
                FloatingActionButton(
                    onClick = {
                        QuestionCropActivity.sendIntent(context)
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .padding(24.dp)
                        .size(80.dp),
                    backgroundColor = colorResource(R.color.main_color)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_camera),
                        null,
                        tint = colorResource(R.color.white),
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun Drawer(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    Column(
        modifier
            .fillMaxSize()
            .background(Color.White),

        ) {

        val isLogin = true

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
                color = colorResource(R.color.black),
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
                        tint = colorResource(R.color.gray600),
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        stringResource(texts[i]),
                        fontFamily = mainFont,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.gray500),
                    )
                }
            }

            CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
                Button(
                    onClick = {
                        LoginActivity.sendIntent(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.main_color)),
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
        } else {

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
                    Text(
                        "آنیتا علیخانی",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(R.color.black),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "+989123456789",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(R.color.gray500),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
                IconButton(onClick = {
                    ProfileEditActivity.sendIntent(context)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
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
                        color = colorResource(R.color.main_color),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_my_questions),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colorResource(R.color.main_color)
                    )
                }
            }
            Box(
                Modifier
                    .padding(0.dp, 4.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .clickable {

                    }) {
                Row(
                    Modifier
                        .padding(24.dp, 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.send_review, stringResource(R.string.app_name)),
                        modifier = Modifier
                            .weight(1f),
                        color = colorResource(R.color.main_color),
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colorResource(R.color.main_color)
                    )
                }
            }
            Box(
                Modifier
                    .padding(0.dp, 4.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .clickable {

                    }) {
                Row(
                    Modifier
                        .padding(24.dp, 12.dp)
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

        }

        Spacer(modifier = Modifier.weight(1F))


        val darkState = remember { mutableStateOf(false) }


        Row(
            modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.background_color),
                color = colorResource(R.color.main_color),
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
                onCheckedChange = { darkState.value = it },
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
