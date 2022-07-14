package com.nimkat.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.DataHolder
import com.nimkat.app.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _authModel = MutableLiveData<DataHolder<AuthModel>>(DataHolder.pure())
    private val _isCodeSent = MutableLiveData<DataHolder<Boolean>>(DataHolder.pure())
    val isCodeSentLiveData: LiveData<DataHolder<Boolean>> = _isCodeSent
    val authModelLiveData: LiveData<DataHolder<AuthModel>> = _authModel

    init {
//        getCode("+989174568295")
    }

    fun getCode(phoneNumber: String) {
        Log.d("Main Activity", "1")

        _isCodeSent.postValue(DataHolder.loading())
        viewModelScope.launch {
            val response = repository.getCode(phoneNumber)
            if (response.isSuccessful) {
                Log.d("Main Activity", "2")
                _isCodeSent.postValue(DataHolder.success(true))
            }else {
                Log.d("Main Activity", "3")
                _isCodeSent.postValue(DataHolder.error())
            }
        }
    }

    fun verifyCode(smsCode: String) {
        _authModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            val response = repository.verifyCode(smsCode)
            if (response != null && response.isSuccessful && response.body() != null) {
                _authModel.postValue(DataHolder.success(response.body()!!))
            }else {
                _authModel.postValue(DataHolder.error())
            }
        }
    }
}