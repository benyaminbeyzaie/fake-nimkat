package com.nimkat.app.view.otp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.SMS.SmsReciever
import com.nimkat.app.view.CircularIndeterminanteProgressBar
import com.nimkat.app.view.SnackBar
import com.nimkat.app.view.main.MainActivity
import com.nimkat.app.view.profile_edit.CompleteProfile
import com.nimkat.app.view_model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class OtpActivity : AppCompatActivity() {

    companion object {
        fun sendIntent(context: Context, id: String, mobile: String) =
            Intent(context, OtpActivity::class.java).apply {
                putExtra("id", id)
                putExtra("mobile" , mobile)
                context.startActivity(this)
            }
    }

    private var REQ_USER_CONSENT = 200
    var smsBroadcastReciver: SmsReciever? = null
    var smsCode = ""
    var id:String = ""
    var mobile: String = ""

    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startSmartUserConsent()

        id = intent.getStringExtra("id").orEmpty()
        mobile = intent.getStringExtra("mobile").orEmpty()

        contentSetter()
    }

    private fun contentSetter() {
        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        OtpContent(id, authViewModel, smsCode , mobile)
                    }
                }
            }
        }
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)

    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReciver = SmsReciever()
        smsBroadcastReciver!!.smsBroadcastReciverListener =
            object : SmsReciever.SmsBroadcastReciverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent, REQ_USER_CONSENT)
                }

                override fun onFailure() {

                }
            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReciver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        val otpPatter = Pattern.compile("(|^)\\d{5}")
        val matcher = otpPatter.matcher(message)
        if (matcher.find()) {

            smsCode = matcher.group(0)!!
            contentSetter()

            if (smsCode != null && smsCode != ""){
                authViewModel.verifyCode(smsCode, id)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReciver)
    }

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun OtpContent(id: String, authViewModel: AuthViewModel, smsCode: String, mobile: String) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    val errorSnackBar = remember { SnackbarHostState() }

    val bool = remember { mutableStateOf(false) }

    val authState = authViewModel.authModelLiveData.observeAsState()
    if (authState.value?.status === DataStatus.Success) {
        MainActivity.sendIntent(context)
    }
    if (authState.value?.status === DataStatus.NeedCompletion) {
        CompleteProfile.sendIntent(context)
    }
    if (authState.value?.status === DataStatus.Error) {
        Log.d("Login", "isCodeSent.value?.status == DataStatus.Error")
        LaunchedEffect(lifecycleOwner.lifecycleScope) {
            errorSnackBar.showSnackbar(
                message = "متاسفانه مشکلی پیش اومده یا دستگاهت به اینترنت متصل نیست!",
                actionLabel = "RED",
                duration = SnackbarDuration.Short
            )
        }
        bool.value = false
    }
    if (authState.value?.status === DataStatus.Loading){
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
                .padding(4.dp, 0.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back), null,
                tint = colorResource(R.color.primary_text),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(180f)
            )
        }

        if(bool.value) {
            Row {
                CircularIndeterminanteProgressBar(true , 20)
            }
        }else{
            Spacer(modifier = Modifier.height(20.dp))
        }

        Text(
            stringResource(R.string.otp_code),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp, 20.dp, 0.dp),
            color = colorResource(R.color.primary_text),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Text(
            stringResource(R.string.otp_desc),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp),
            color = colorResource(R.color.primary_text_variant),
            fontFamily = mainFont,
            fontSize = 14.sp
        )
        Text(
            "+98$mobile",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp),
            color = colorResource(R.color.primary_text_variant),
            fontFamily = mainFont,
            fontSize = 14.sp,
            textAlign = TextAlign.Right,
            style = TextStyle(textDirection = TextDirection.Ltr)
        )

        val items: List<MutableState<String>>
        if (smsCode == "") {
            items = listOf(
                remember { mutableStateOf("") },
                remember { mutableStateOf("") },
                remember { mutableStateOf("") },
                remember { mutableStateOf("") },
                remember { mutableStateOf("") },
            )
        } else {
            items = listOf(
                remember { mutableStateOf(smsCode[0].toString()) },
                remember { mutableStateOf(smsCode[1].toString()) },
                remember { mutableStateOf(smsCode[2].toString()) },
                remember { mutableStateOf(smsCode[3].toString()) },
                remember { mutableStateOf(smsCode[4].toString()) },
            )
        }

        val activates = listOf(
            remember { FocusRequester() },
            remember { FocusRequester() },
            remember { FocusRequester() },
            remember { FocusRequester() },
            remember { FocusRequester() },
        )

        val keyboardController = LocalSoftwareKeyboardController.current

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                for (i in 0..4) {
                    CodeItem(i, items, activates, keyboardController, authViewModel, id)
                }
            }
        }
    }
    
    SnackBar(snackbarHostState = errorSnackBar, Color.Red, true, {})

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CodeItem(
    index: Int,
    states: List<MutableState<String>>,
    activates: List<FocusRequester>,
    keyboardController: SoftwareKeyboardController?,
    authViewModel: AuthViewModel,
    id: String,
) {
    OutlinedTextField(
        singleLine = true,
        value = states[index].value,
        onValueChange = {
            if (it.length > 1) {
                if (index < 4)
                    activates[index + 1].requestFocus()
                return@OutlinedTextField
            }
            states[index].value = it


            if (it.length == 1) {
                if (index < 4)
                    activates[index + 1].requestFocus()
            }

        },
        modifier = Modifier
            .padding(2.dp)
            .focusRequester(activates[index])
            .size(55.dp)
            .onKeyEvent { event: KeyEvent ->
                if (event.key.keyCode == Key.Backspace.keyCode) {
                    if (index > 0)
                        activates[index - 1].requestFocus()
                }
                false
            },
        shape = RoundedCornerShape(6.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = colorResource(R.color.textfield_background),
            focusedBorderColor = colorResource(R.color.blue),
            unfocusedBorderColor = colorResource(R.color.textfield_background),
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = mainFont,
            color = colorResource(R.color.primary_text),
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                var enteredCode = "";
                for (state in states) {
                    enteredCode += state.value
                }
                Log.d("Opt", "keyboardActions: Done ---> $enteredCode");

                authViewModel.verifyCode(enteredCode, id)
            }
        )
    )
}
