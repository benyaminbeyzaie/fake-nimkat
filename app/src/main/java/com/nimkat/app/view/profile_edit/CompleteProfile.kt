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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.nimkat.app.utils.ASK_GRADE_CODE
import com.nimkat.app.utils.ASK_NAME_CODE
import com.nimkat.app.utils.CROP_IMAGE_CODE
import com.nimkat.app.view.otp.OtpActivity
import com.nimkat.app.view.profile_edit.grade.GradeActivity
import com.nimkat.app.view.profile_edit.grade.GradeContent
import com.nimkat.app.view.profile_edit.ui.theme.NimkatTheme
import com.nimkat.app.view.question_crop.QuestionCropActivity

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

    fun askForGrade(){
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
            ASK_GRADE_CODE ->{
                if (resultCode == Activity.RESULT_OK) {
                    data?.apply {
                        grade = getStringExtra("grade")!!
                        gradeID = getIntExtra("gradeID" , 0)
                        a()
                    }
                } else {
                    Log.d("kiloURI", "IMAGE CROPPING CANCELED.")
                    Toast.makeText(this, "IMAGE CROPPING CANCELED.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    fun a(){
        setContent {
            com.nimkat.app.ui.theme.NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column() {
                        Text(text = name)
                        Text(text = grade)
                        Text(text = "grade is: $gradeID")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String) {
    Text(text = "Hello $name!")
}

