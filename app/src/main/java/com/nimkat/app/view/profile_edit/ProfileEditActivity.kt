package com.nimkat.app.view.profile_edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimkat.app.R
import com.nimkat.app.view.profile_edit.grade.GradeActivity
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont


class ProfileEditActivity : ComponentActivity() {

    companion object {
        fun sendIntent(context: Context) = Intent(context, ProfileEditActivity::class.java).apply {
            context.startActivity(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        ProfileEditContent()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfileEditContent() {

    val context = LocalContext.current

    val username = remember {
        mutableStateOf("آنیتا علیخانی")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_back))
    ) {
        IconButton(
            onClick = {
                if (context is Activity) context.onBackPressed()
            },
            modifier = Modifier
                .padding(4.dp, 12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back), null,
                tint = colorResource(R.color.black),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(180f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            stringResource(R.string.profile_edit),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 0.dp),
            color = colorResource(R.color.black),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "+989123456789",
            onValueChange = {
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp),
            label = {
                Text(
                    stringResource(id = R.string.mobile),
                    fontFamily = mainFont,
                    color = colorResource(
                        R.color.color_hint
                    ),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Left
                )
            },
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(R.color.main_color),
                unfocusedBorderColor = colorResource(R.color.gray500),
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = mainFont,
                color = colorResource(R.color.black),
                textAlign = TextAlign.Left
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = username.value,
            onValueChange = {
                username.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp),
            label = {
                Text(
                    stringResource(id = R.string.username),
                    fontFamily = mainFont,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Left
                )
            },
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(R.color.main_color),
                focusedLabelColor = colorResource(R.color.main_color),
                unfocusedLabelColor = colorResource(R.color.gray500),
                unfocusedBorderColor = colorResource(R.color.gray500),
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = mainFont,
                color = colorResource(R.color.black),
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .padding(16.dp, 0.dp)
        ) {
            OutlinedTextField(
                value = "چهارم",
                onValueChange = {
                },
                enabled = false,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp, 0.dp),
                label = {
                    Text(
                        stringResource(id = R.string.grade),
                        fontFamily = mainFont,
                        color = colorResource(
                            R.color.color_hint
                        ),
                        fontSize = 12.sp,
                    )
                },
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(R.color.main_color),
                    unfocusedBorderColor = colorResource(R.color.gray500),
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = mainFont,
                    color = colorResource(R.color.black),
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
            Box(modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterVertically)
                .padding(4.dp, 0.dp)
                .clickable {
                    GradeActivity.sendIntent(context)
                }) {
                Text(
                    stringResource(R.string.change),
                    modifier = Modifier
                        .padding(12.dp, 6.dp),
                    color = colorResource(R.color.main_color),
                    fontFamily = mainFont,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.main_color)),
            ) {
                Row {
                    Text(
                        text = stringResource(R.string.ok),
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
}