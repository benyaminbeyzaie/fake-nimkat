package com.nimkat.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimkat.app.models.AuthModel
import com.nimkat.app.models.DataHolder
import com.nimkat.app.models.ProfileModel
import com.nimkat.app.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _authModel = MutableLiveData<DataHolder<AuthModel>>(DataHolder.pure())
    private val _isCodeSent = MutableLiveData<DataHolder<Int>>(DataHolder.pure())
    val isCodeSentLiveData: LiveData<DataHolder<Int>> = _isCodeSent
    val authModelLiveData: LiveData<DataHolder<AuthModel>> = _authModel
    private val _profileModel = MutableLiveData<DataHolder<ProfileModel>>(DataHolder.pure())


    val profileModelLiveData: LiveData<DataHolder<ProfileModel>> = _profileModel

    private fun initProfile() {
        Log.d("Prof View Model", "init prof called")
        val profileModel = repository.initProfile() ?: return
        _profileModel.postValue(DataHolder.success(profileModel))
    }


    fun initAuth(shouldInitProfile: Boolean = true, firebaseToken: String? = null) {
        Log.d("Auth View Model", "init auth called " + _authModel.value?.data.toString())
        if (_authModel.value?.data != null && firebaseToken == null) return
        val authModel = repository.initAuth(firebaseToken) ?: return
        _authModel.postValue(DataHolder.success(authModel))
        Log.d("Auth View Model", "Auth Model is loaded")
        if (shouldInitProfile) {
            initProfile()
        }
    }

    fun getCode(phoneNumber: String) {
        _isCodeSent.postValue(DataHolder.loading())
        viewModelScope.launch {
            try {
                val response = repository.getCode(phoneNumber)
                Log.d("Response", response.message())
                if (response.isSuccessful && response.body() != null) {
                    _isCodeSent.postValue(DataHolder.success(response.body()!!.smsCode))
                } else {
                    _isCodeSent.postValue(DataHolder.error())
                }
            }catch (e: Exception){
                Log.d("Response" , "an error Eccured")
                _isCodeSent.postValue(DataHolder.error())
            }
        }
    }

    fun verifyCode(smsCode: String, id: String) {
        _authModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            try {
                val response = repository.verifyCode(smsCode, id)
                if (response != null && response.isSuccessful && response.body() != null) {
                    if (!response.body()!!.isProfileCompleted) {
                        _authModel.postValue(DataHolder.needCompletion(response.body()!!))
                        Log.d("AUTH NEED ", response.body()!!.toString())
                    } else {
                        _profileModel.postValue(DataHolder.loading())
                        val profileResponse = repository.getProfile(id)
                        if (profileResponse != null && profileResponse.isSuccessful && profileResponse.body() != null) {
                            Log.d("PROF", profileResponse.body()!!.toString())
                            _profileModel.postValue(DataHolder.success(profileResponse.body()!!))
                            Log.d("PROF", "LOAD Into Profile Model ")
                        } else {
                            _profileModel.postValue(DataHolder.error())
                            Log.d("PROF", "COULD NOT LOAD ")
                        }
                        _authModel.postValue(DataHolder.success(response.body()!!))

                    }
                } else {
                    _authModel.postValue(DataHolder.error())
                }
            }catch (e: Exception){
                _authModel.postValue(DataHolder.error())
            }
        }
    }

    fun clearAuth() {
        viewModelScope.launch {
            _authModel.postValue(DataHolder.pure())
            _profileModel.postValue(DataHolder.pure())
            repository.clearAuth()
        }
    }

    fun update(name: String, gradeId: Int) {
        var data = _profileModel.value?.data
        _profileModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            try {
                val response = repository.updateProfile(
                    name,
                    gradeId,
                )
                if (response != null && response.isSuccessful && response.body() != null) {
                    Log.d("update ", response.body()!!.toString())
                    _profileModel.postValue(DataHolder.success(response.body()!!))
                    Log.d("update ", "LOAD Into Profile Model ")
                } else {
                    Log.d("update ", "update error" + response.toString())
                    _profileModel.postValue(DataHolder.error())
                }
            }catch (e: Exception){
                _profileModel.postValue(DataHolder.errorWithData(data = data!!))
                Log.d("update ", "update more error ")
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete()
            _authModel.postValue(DataHolder.pure())
            _profileModel.postValue(DataHolder.pure())
        }
    }
}