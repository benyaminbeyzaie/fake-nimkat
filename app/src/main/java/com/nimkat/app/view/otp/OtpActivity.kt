package com.nimkat.app.view.otp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.CROP_IMAGE_CODE
import com.nimkat.app.utils.MOBILE
import com.nimkat.app.view.main.MainActivity
import com.nimkat.app.view.profile_edit.CompleteProfile
import com.nimkat.app.view.question_crop.QuestionCropActivity
import com.nimkat.app.view_model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpActivity : ComponentActivity() {

    companion object {
        fun sendIntent(context: Context, id: String) =
            Intent(context, OtpActivity::class.java).apply {
                putExtra("id", id)
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
                        OtpContent(intent.getStringExtra("id").orEmpty(), viewModel())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpContent(id: String, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val authState = authViewModel.authModelLiveData.observeAsState()
    Log.d("OptActivity", "auth state: ${authState.value?.status}")
    if (authState.value?.status === DataStatus.Success) {
        MainActivity.sendIntent(context)
    }
    if (authState.value?.status === DataStatus.NeedCompletion){
        CompleteProfile.sendIntent(context)
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
            stringResource(R.string.otp_code),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 0.dp),
            color = colorResource(R.color.primary_text),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Text(
            stringResource(R.string.otp_desc).plus("\n").plus(""),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp),
            color = colorResource(R.color.primary_text_variant),
            fontFamily = mainFont,
            fontSize = 14.sp
        )


        val items = listOf(
            remember { mutableStateOf("") },
            remember { mutableStateOf("") },
            remember { mutableStateOf("") },
            remember { mutableStateOf("") },
            remember { mutableStateOf("") },
        )

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
    var current = LocalContext.current
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
