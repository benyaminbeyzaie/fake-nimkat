package com.nimkat.app.view.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.view.CircularIndeterminanteProgressBar
import com.nimkat.app.view.SnackBar
import com.nimkat.app.view.otp.OtpActivity
import com.nimkat.app.view_model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

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
                    color = colorResource(id = R.color.background)
                ) {

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        LoginContent(viewModel())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginContent(authViewModel: AuthViewModel) {
    val mobile = remember { mutableStateOf("") }
    val inviteCode = remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isCodeSent = authViewModel.isCodeSentLiveData.observeAsState()
    val errorSnackBar = remember { SnackbarHostState() }
    val bool = remember { mutableStateOf(false) }


    if (isCodeSent.value?.status == DataStatus.Success) {
        Log.d("Login", "isCodeSent.value?.status == DataStatus.Success")
//        bool.value = false
        OtpActivity.sendIntent(context, isCodeSent.value!!.data!!.toString() , mobile.value)
    } else if (isCodeSent.value?.status == DataStatus.Error) {
        Log.d("Login", "isCodeSent.value?.status == DataStatus.Error")

        LaunchedEffect(lifecycleOwner.lifecycleScope) {
            errorSnackBar.showSnackbar(
                message = "متاسفانه مشکلی پیش اومده یا دستگاهت به اینترنت متصل نیست!",
                actionLabel = "RED",
                duration = SnackbarDuration.Short
            )
        }
        bool.value = false
    }else if (isCodeSent.value?.status == DataStatus.Loading){
        bool.value = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
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
                tint = colorResource(R.color.primary_text),
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
            color = colorResource(R.color.primary_text),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Text(
            stringResource(R.string.login_desc),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp),
            color = colorResource(R.color.primary_text_variant),
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
                    backgroundColor = colorResource(R.color.textfield_background),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = mainFont,
                    color = colorResource(R.color.primary_text),
                    textAlign = TextAlign.Left,
                    textDirection = TextDirection.Ltr
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
                        .background(colorResource(R.color.textfield_background))
                ) {
                    Text(
                        "+98",
                        color = colorResource(R.color.primary_text),
                        modifier = Modifier
                            .wrapContentSize(),
                        fontSize = 14.sp,
                        style = TextStyle(textDirection = TextDirection.Ltr)
                    )
                }
            }

        }

        Text(
            stringResource(R.string.login_invite_code),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 12.dp, 20.dp, 4.dp),
            color = colorResource(R.color.primary_text_variant),
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
                backgroundColor = colorResource(R.color.textfield_background),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = mainFont,
                color = colorResource(R.color.primary_text),
                textAlign = TextAlign.Left,
                textDirection = TextDirection.Ltr
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
//                    bool.value = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.blue)),
                enabled = (!bool.value)
            ) {
                if (!bool.value){
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
                }else{
                    CircularIndeterminanteProgressBar(true , 0)
                }

            }
        }
    }
    SnackBar(snackbarHostState = errorSnackBar, Color.Red, true, {})

}

