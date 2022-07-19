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
import com.nimkat.app.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository  , private val profileRepository: ProfileRepository) : ViewModel() {

    private val _authModel = MutableLiveData<DataHolder<AuthModel>>(DataHolder.pure())
    private val _isCodeSent = MutableLiveData<DataHolder<Int>>(DataHolder.pure())
    val isCodeSentLiveData: LiveData<DataHolder<Int>> = _isCodeSent
    val authModelLiveData: LiveData<DataHolder<AuthModel>> = _authModel

    private val _profileModel = MutableLiveData<DataHolder<ProfileModel>>(DataHolder.pure())
    val profileModelLiveData: LiveData<DataHolder<ProfileModel>> = _profileModel
    private val _isProfileUpdated = MutableLiveData<DataHolder<Int>>(DataHolder.pure())
    val isProfileUpdatedLiveData: LiveData<DataHolder<Int>> = _isProfileUpdated

    fun initProf() {
        Log.d("Auth View Model", "init auth called")
        val profileModel = profileRepository.initProfile() ?: return
        _profileModel.postValue(DataHolder.success(profileModel))
    }


    fun initAuth() {
        Log.d("Auth View Model", "init auth called")
        val authModel = repository.initAuth() ?: return
        _authModel.postValue(DataHolder.success(authModel))
        initProf()
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
                if (!response.body()!!.isProfileCompleted){
                    _authModel.postValue(DataHolder.needCompletion())
                }else {
                    _authModel.postValue(DataHolder.success(response.body()!!))
                    getProfile(response.body()!!.userId.toString() , "Token " + response.body()!!.token)
                }
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

    fun update(name: String , gradeID :Int  , id: String) {
        _profileModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            val response = repository.updateProfile(name, gradeID, id)
            if (response != null && response.isSuccessful && response.body() != null) {
                _profileModel.postValue(DataHolder.success(response.body()!!))
            }else {
                _profileModel.postValue(DataHolder.error())
            }
        }
    }

    fun getProfile(id: String , token: String){
        _profileModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            val response = profileRepository.getProfile(id , token)
            if (response != null && response.isSuccessful && response.body() != null) {
                _profileModel.postValue(DataHolder.success(response.body()!!))
            }else {
                _profileModel.postValue(DataHolder.error())
            }
        }
    }


}