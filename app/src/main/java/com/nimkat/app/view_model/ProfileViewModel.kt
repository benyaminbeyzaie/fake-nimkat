package com.nimkat.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimkat.app.models.DataHolder
import com.nimkat.app.models.ProfileModel
import com.nimkat.app.repository.AuthRepository
import com.nimkat.app.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel()  {
    private val _profileModel = MutableLiveData<DataHolder<ProfileModel>>(DataHolder.pure())
    val profileModelLiveData: LiveData<DataHolder<ProfileModel>> = _profileModel
    private val _isProfileUpdated = MutableLiveData<DataHolder<Int>>(DataHolder.pure())
    val isProfileUpdatedLiveData: LiveData<DataHolder<Int>> = _isProfileUpdated

    fun initAuth() {
        Log.d("Auth View Model", "init auth called")
        val profileModel = repository.initProfile() ?: return
        _profileModel.postValue(DataHolder.success(profileModel))
    }

    fun update(name: String , gradeID :Int  , id: String , token: String) {
        _profileModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            val response = repository.updateProfile(name, gradeID, id , token)
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
            val response = repository.getProfile(id , token)
            if (response != null && response.isSuccessful && response.body() != null) {
                _profileModel.postValue(DataHolder.success(response.body()!!))
            }else {
                _profileModel.postValue(DataHolder.error())
            }
        }
    }

    fun clearAuth() {
        _profileModel.postValue(DataHolder.loading())
        viewModelScope.launch {
            repository.clearAuth()
            _profileModel.postValue(DataHolder.pure())
        }
    }
}