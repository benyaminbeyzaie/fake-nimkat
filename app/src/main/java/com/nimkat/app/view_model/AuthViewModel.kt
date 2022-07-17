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
import javax.inject.Singleton

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _authModel = MutableLiveData<DataHolder<AuthModel>>(DataHolder.pure())
    private val _isCodeSent = MutableLiveData<DataHolder<Int>>(DataHolder.pure())
    val isCodeSentLiveData: LiveData<DataHolder<Int>> = _isCodeSent
    val authModelLiveData: LiveData<DataHolder<AuthModel>> = _authModel

    fun initAuth() {
        Log.d("Auth View Model", "init auth called")
        val authModel = repository.initAuth() ?: return
        _authModel.postValue(DataHolder.success(authModel))
    }

    fun getCode(phoneNumber: String) {
        _isCodeSent.postValue(DataHolder.loading())
        viewModelScope.launch {
            val response = repository.getCode(phoneNumber)
            if (response.isSuccessful && response.body() != null) {
                _isCodeSent.postValue(DataHolder.success(response.body()!!.smsCode))
            }else {
                _isCodeSent.postValue(DataHolder.error())
            }
        }
    }

    fun verifyCode(smsCode: String, id: String) {
        _authModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            val response = repository.verifyCode(smsCode, id)
            if (response != null && response.isSuccessful && response.body() != null) {
                _authModel.postValue(DataHolder.success(response.body()!!))
            }else {
                _authModel.postValue(DataHolder.error())
            }
        }
    }

    fun clearAuth() {
        _authModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            repository.clearAuth()
            _authModel.postValue(DataHolder.pure())
        }
    }
}