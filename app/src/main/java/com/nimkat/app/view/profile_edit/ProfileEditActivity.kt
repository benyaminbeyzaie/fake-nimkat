package com.nimkat.app.view.profile_edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.nimkat.app.R
import com.nimkat.app.models.EducationalGrade
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.ASK_FOR_EDIT_PROFILE
import com.nimkat.app.utils.ASK_GRADE_CODE
import com.nimkat.app.utils.CROP_IMAGE_CODE
import com.nimkat.app.view.profile_edit.grade.GradeActivity
import kotlinx.coroutines.launch
import kotlin.math.log


class ProfileEditActivity : AppCompatActivity() {

    companion object {
        fun sendIntent(context: Context, name: String, phone: String, grade: EducationalGrade) =
            Intent(context, ProfileEditActivity::class.java).apply {
                this.putExtra("name", name)
                this.putExtra("phone", phone)
                this.putExtra("grade", grade.name)
                (context as Activity).startActivityForResult(this , ASK_FOR_EDIT_PROFILE)
            }
    }

    var name: String? = null
    var phone: String? = null
    var grade: String? = null
    var gradeID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        name = intent.getStringExtra("name")
        phone = intent.getStringExtra("phone")
        grade = intent.getStringExtra("grade")

        Log.d("grade", grade.toString())

        contentSetter()
    }


    fun contentSetter(){
        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        ProfileEditContent(name, phone, grade , gradeID)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val x = this
        when (requestCode) {
            ASK_GRADE_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("GradeAct" , "result recieved")
                    data?.apply {
                        grade = getStringExtra("grade")!!
                        gradeID = getIntExtra("gradeID" , 0)

                    }
                    contentSetter()
                } else {
                    Log.d("kiloURI", "IMAGE CROPPING CANCELED.")
                    Toast.makeText(this, "IMAGE CROPPING CANCELED.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}

//@Preview
@Composable
fun ProfileEditContent(name: String?, phone: String?, grade: String? , gradeID: Int) {

    val context = LocalContext.current

    val username = remember {
        mutableStateOf(name)
    }
    val phoneNumber = remember {
        mutableStateOf(phone)
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
            stringResource(R.string.profile_edit),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 0.dp),
            color = colorResource(R.color.primary_text),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 0.dp)
        ) {

            OutlinedTextField(
                value = phoneNumber.value?.substring(3) ?: "9171234567",
                onValueChange = {
                },
                enabled = false,
                modifier = Modifier
                    .weight(1f)
                    .height(65.dp),
                label = {
                    Text(
                        stringResource(id = R.string.mobile),
                        fontFamily = mainFont,
                        color = colorResource(
                            R.color.primary_text_variant
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
                    color = colorResource(R.color.primary_text),
                    textAlign = TextAlign.Left
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = "+98",
                onValueChange = {
                },
                enabled = false,
                modifier = Modifier
                    .width(67.dp)
                    .height(67.dp)
                    .align(Alignment.CenterVertically)
                    .padding(0.dp, 8.dp, 0.dp, 0.dp),
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(R.color.main_color),
                    unfocusedBorderColor = colorResource(R.color.gray500),
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = mainFont,
                    color = colorResource(R.color.primary_text),
                    textAlign = TextAlign.Center,
                    textDirection = TextDirection.Ltr
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = username.value ?: "آنیتا علیخانی",
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
                    textAlign = TextAlign.Left,
                    color = colorResource(id = R.color.primary_text_variant)
                )
            },
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(R.color.blue),
                focusedLabelColor = colorResource(R.color.blue),
                unfocusedLabelColor = colorResource(R.color.gray500),
                unfocusedBorderColor = colorResource(R.color.gray500),
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = mainFont,
                color = colorResource(R.color.primary_text),
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .padding(16.dp, 0.dp)
        ) {
            OutlinedTextField(
                value = grade ?: "اول",
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
                            R.color.primary_text_variant
                        ),
                        fontSize = 12.sp,
                    )
                },
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(R.color.blue),
                    unfocusedBorderColor = colorResource(R.color.gray500),
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = mainFont,
                    color = colorResource(R.color.primary_text),
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
                    color = colorResource(R.color.blue),
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
                    val data = Intent().apply {
                        putExtra("grade", grade)
                        putExtra("gradeID" , gradeID)
                        putExtra("name" , username.value)
                    }
                    if (context is Activity) {
                        context.setResult(Activity.RESULT_OK, data)
                        context.finish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
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
                }
            }
        }
    }
}