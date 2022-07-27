package com.nimkat.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimkat.app.models.DataHolder
import com.nimkat.app.models.DiscoveryAnswers
import com.nimkat.app.repository.TextQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextQuestionViewModel @Inject constructor(
    private val repository: TextQuestionRepository,
) : ViewModel() {
    private var _discoveryAnswers =
        MutableLiveData<DataHolder<ArrayList<DiscoveryAnswers>>>(DataHolder.pure())
    var discoveryAnswers: LiveData<DataHolder<ArrayList<DiscoveryAnswers>>> = _discoveryAnswers

    fun sendQuestion(question: String) {
        _discoveryAnswers.postValue(DataHolder.loading())

        viewModelScope.launch {
            if (repository.isAuth() == null){
                _discoveryAnswers.postValue(DataHolder.needLogin())
                return@launch
            }

            val response = repository.sendQuestion(question)
            if (response == null || !response.isSuccessful) {
                if (_discoveryAnswers.value != null && _discoveryAnswers.value!!.data != null) {
                    _discoveryAnswers.postValue(DataHolder.success(_discoveryAnswers.value!!.data!!))
                } else {
                    _discoveryAnswers.postValue(DataHolder.error())
                }
            } else {
                if (response.body() == null || response.body()!!.discovery_answers.isNullOrEmpty()) {
                    if (_discoveryAnswers.value == null || _discoveryAnswers.value!!.data == null) {
                        _discoveryAnswers.postValue(
                            DataHolder.pure()
                        )
                    } else {
                        _discoveryAnswers.postValue(
                            DataHolder.success(_discoveryAnswers.value!!.data!!)
                        )
                    }
                } else if (_discoveryAnswers.value != null && _discoveryAnswers.value!!.data == null) {
                    _discoveryAnswers.postValue(DataHolder.success(response.body()!!.discovery_answers!!));
                } else if (_discoveryAnswers.value != null) {
                    _discoveryAnswers.value!!.data!!.addAll(response.body()!!.discovery_answers!!)
                    _discoveryAnswers.postValue(DataHolder.success(_discoveryAnswers.value!!.data!!))
                }
            }

        }
    }
}