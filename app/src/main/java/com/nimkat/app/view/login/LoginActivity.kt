package com.nimkat.app.view.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.view.otp.OtpActivity
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.view_model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    companion object {
        fun sendIntent(context: Context) = Intent(context, LoginActivity::class.java).apply {
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
                        LoginContent(viewModel())
                    }
                }
            }
        }
    }
}

@Composable
fun LoginContent(authViewModel: AuthViewModel) {
    val mobile = remember { mutableStateOf("") }
    val inviteCode = remember { mutableStateOf("") }
    val context = LocalContext.current
    val isCodeSent = authViewModel.isCodeSentLiveData.observeAsState()

    if (isCodeSent.value?.status == DataStatus.Success) {
        Log.d("Login", "isCodeSent.value?.status == DataStatus.Success")
        OtpActivity.sendIntent(context, isCodeSent.value!!.data!!.toString())
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
            stringResource(R.string.free_sign_in_login),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 0.dp),
            color = colorResource(R.color.black),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Text(
            stringResource(R.string.login_desc),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp),
            color = colorResource(R.color.gray500),
            fontFamily = mainFont,
            fontSize = 14.sp
        )


        Row(
            modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 0.dp)
        ) {

            TextField(
                value = mobile.value,
                onValueChange = {
                    mobile.value = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .weight(1F),
                placeholder = {
                    Text(
                        stringResource(id = R.string.mobile_hint),
                        fontFamily = mainFont,
                        color = colorResource(
                            R.color.color_hint
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Left
                    )
                },
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(R.color.gray300),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = mainFont,
                    color = colorResource(R.color.black),
                    textAlign = TextAlign.Left
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
            Card(
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .size(55.dp)
                    .align(Alignment.CenterVertically),
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .background(colorResource(R.color.gray300))
                ) {
                    Text(
                        "+98",
                        color = colorResource(R.color.color_hint),
                        modifier = Modifier
                            .wrapContentSize(),
                        fontSize = 14.sp
                    )
                }
            }

        }

        Text(
            stringResource(R.string.login_invite_code),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 12.dp, 20.dp, 4.dp),
            color = colorResource(R.color.gray500),
            fontFamily = mainFont,
            fontSize = 14.sp
        )
        TextField(
            value = inviteCode.value,
            onValueChange = {
                inviteCode.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(20.dp, 0.dp),
            placeholder = {
                Text(
                    stringResource(id = R.string.invite_code),
                    fontFamily = mainFont,
                    color = colorResource(
                        R.color.color_hint
                    ),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Left
                )
            },
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(R.color.gray300),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = mainFont,
                color = colorResource(R.color.black),
                textAlign = TextAlign.Left
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )

        CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
            Button(
                onClick = {
                    Log.d("LoginActivity", "getting code with phone: +98" + mobile.value);
                    authViewModel.getCode("+98" + mobile.value);
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
                        text = stringResource(R.string.get_otp_code),
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
