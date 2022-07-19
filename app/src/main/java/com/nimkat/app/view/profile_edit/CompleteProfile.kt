package com.nimkat.app.view.profile_edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nimkat.app.R
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.ASK_GRADE_CODE
import com.nimkat.app.utils.ASK_NAME_CODE
import com.nimkat.app.utils.CROP_IMAGE_CODE
import com.nimkat.app.view.main.MainActivity
import com.nimkat.app.view.otp.OtpActivity
import com.nimkat.app.view.profile_edit.grade.GradeActivity
import com.nimkat.app.view.profile_edit.grade.GradeContent
import com.nimkat.app.view.profile_edit.ui.theme.NimkatTheme
import com.nimkat.app.view.question_crop.QuestionCropActivity
import com.nimkat.app.view_model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteProfile : ComponentActivity() {

    companion object {
        fun sendIntent(context: Context) =
            Intent(context, CompleteProfile::class.java).apply {
                context.startActivity(this)
            }
    }


    var name: String = "default"
    var grade: String = "default"
    var gradeID: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val intent = Intent(this@CompleteProfile, WhatsYourNameActivity::class.java)
        startActivityForResult(intent, ASK_NAME_CODE)
    }

    fun askForGrade() {
        val intent = Intent(this@CompleteProfile, GradeActivity::class.java)
        startActivityForResult(intent, ASK_GRADE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val x = this
        when (requestCode) {
            ASK_NAME_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.apply {
                        name = getStringExtra("name")!!
                        askForGrade()
                    }
                } else {
                    Log.d("kiloURI", "IMAGE CROPPING CANCELED.")
                    Toast.makeText(this, "IMAGE CROPPING CANCELED.", Toast.LENGTH_SHORT).show()
                }
            }
            ASK_GRADE_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.apply {
                        grade = getStringExtra("grade")!!
                        gradeID = getIntExtra("gradeID", 0)
                        val authViewModel: AuthViewModel by viewModels()
                        authViewModel.initAuth()
                        a(authViewModel)
                    }
                } else {
                    Log.d("kiloURI", "IMAGE CROPPING CANCELED.")
                    Toast.makeText(this, "IMAGE CROPPING CANCELED.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    fun a(viewModel: AuthViewModel) {
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Greeting3(name = name, grade = grade, gradeID = gradeID , viewModel)
            }

        }
    }
}

@Composable
fun Greeting3(name: String = "mojtaba", grade: String = "یازدهم", gradeID: Int = 11 , viewModel: AuthViewModel) {
    com.nimkat.app.ui.theme.NimkatTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {


            val context = LocalContext.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.background))
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Column() {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.name),
                            color = colorResource(R.color.primary_text),
                            fontFamily = secondFont,
                            fontSize = 25.sp
                        )
                        Text(
                            text = " : ",
                            color = colorResource(R.color.primary_text),
                            fontFamily = secondFont,
                            fontSize = 25.sp
                        )
                        Text(
                            text = name,
                            color = colorResource(R.color.primary_text_variant),
                            fontFamily = secondFont,
                            fontSize = 25.sp
                        )
                    }
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.grade),
                            color = colorResource(R.color.primary_text),
                            fontFamily = secondFont,
                            fontSize = 25.sp
                        )
                        Text(
                            text = " : ",
                            color = colorResource(R.color.primary_text),
                            fontFamily = secondFont,
                            fontSize = 25.sp
                        )
                        Text(
                            text = grade,
                            color = colorResource(R.color.primary_text_variant),
                            fontFamily = secondFont,
                            fontSize = 25.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
                    Button(
                        onClick = {
//                            MainActivity.update(name , gradeID , grade)
                            viewModel.update(name , gradeID , grade)
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
                                text = stringResource(R.string.ok),
                                style = TextStyle(
                                    fontFamily = secondFont
                                ),
                                color = colorResource(R.color.white),
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Outlined.CheckCircle,
                                null,
                                tint = colorResource(R.color.white),
                                modifier = Modifier.padding(0.dp, 6.dp, 0.dp, 0.dp)
                            )
                        }
                    }


                }
            }


        }
    }
}
