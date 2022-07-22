package com.nimkat.app.view.profile_edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nimkat.app.utils.ASK_GRADE_CODE
import com.nimkat.app.utils.ASK_NAME_CODE
import com.nimkat.app.view.main.MainActivity
import com.nimkat.app.view.profile_edit.grade.GradeActivity
import com.nimkat.app.view_model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteProfile : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

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
        authViewModel.profileModelLiveData.observe(this) { value ->
            if (value.data?.isProfileCompleted == true) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        val intent = Intent(this@CompleteProfile, WhatsYourNameActivity::class.java)
        startActivityForResult(intent, ASK_NAME_CODE)
    }

    private fun askForGrade() {
        val intent = Intent(this@CompleteProfile, GradeActivity::class.java)
        startActivityForResult(intent, ASK_GRADE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                        authViewModel.initAuth()
                        authViewModel.update(name , gradeID)
                    }
                } else {
                    Log.d("kiloURI", "IMAGE CROPPING CANCELED.")
                    Toast.makeText(this, "IMAGE CROPPING CANCELED.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
