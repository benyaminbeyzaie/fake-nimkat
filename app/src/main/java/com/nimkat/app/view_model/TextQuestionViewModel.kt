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
    private var _myQuestions =
        MutableLiveData<DataHolder<ArrayList<DiscoveryAnswers>>>(DataHolder.pure())
    var myQuestions: LiveData<DataHolder<ArrayList<DiscoveryAnswers>>> = _myQuestions

    private var _sendQuestion =
        MutableLiveData<DataHolder<ArrayList<DiscoveryAnswers>>>(DataHolder.pure())
    var sendQuestion: LiveData<DataHolder<ArrayList<DiscoveryAnswers>>> = _sendQuestion

    fun reCreate(){
        _myQuestions =
            MutableLiveData<DataHolder<ArrayList<DiscoveryAnswers>>>(DataHolder.pure())
        myQuestions = _myQuestions
    }

    fun loadQuestions(question: String) {

        _myQuestions.postValue(DataHolder.loading())


        viewModelScope.launch {


            if (repository.isAuth() == null){
                _myQuestions.postValue(DataHolder.needLogin())
                return@launch
            }

            val response = repository.getQuestions(question)
            if (response == null || !response.isSuccessful) {
                if (_myQuestions.value != null && _myQuestions.value!!.data != null) {
                    _myQuestions.postValue(DataHolder.success(_myQuestions.value!!.data!!))
                } else {
                    _myQuestions.postValue(DataHolder.error())
                }
            } else {
                if (response.body() == null || response.body()!!.discovery_answers.isNullOrEmpty()) {
                    if (_myQuestions.value == null || _myQuestions.value!!.data == null) {
                        _myQuestions.postValue(
                            DataHolder.pure()
                        )
                    } else {
                        _myQuestions.postValue(
                            DataHolder.success(_myQuestions.value!!.data!!)
                        )
                    }
                } else if (_myQuestions.value != null && _myQuestions.value!!.data == null) {
                    _myQuestions.postValue(DataHolder.success(response.body()!!.discovery_answers!!));
                } else if (_myQuestions.value != null) {
                    _myQuestions.value!!.data!!.addAll(response.body()!!.discovery_answers!!)
                    _myQuestions.postValue(DataHolder.success(_myQuestions.value!!.data!!))
                }
            }

        }
    }

    fun sendQuestion(question: String) {

        _sendQuestion.postValue(DataHolder.loading())


        viewModelScope.launch {


            if (repository.isAuth() == null){
                _sendQuestion.postValue(DataHolder.needLogin())
                return@launch
            }

            val response = repository.sendQuestion(question)
            if (response == null || !response.isSuccessful) {
                if (_sendQuestion.value != null && _sendQuestion.value!!.data != null) {
                    _sendQuestion.postValue(DataHolder.success(_sendQuestion.value!!.data!!))
                } else {
                    _sendQuestion.postValue(DataHolder.error())
                }
            } else {
                if (response.body() == null || response.body()!!.discovery_answers.isNullOrEmpty()) {
                    if (_sendQuestion.value == null || _sendQuestion.value!!.data == null) {
                        _sendQuestion.postValue(
                            DataHolder.pure()
                        )
                    } else {
                        _sendQuestion.postValue(
                            DataHolder.success(_sendQuestion.value!!.data!!)
                        )
                    }
                } else if (_sendQuestion.value != null && _sendQuestion.value!!.data == null) {
                    _sendQuestion.postValue(DataHolder.success(response.body()!!.discovery_answers!!));
                } else if (_sendQuestion.value != null) {
                    _sendQuestion.value!!.data!!.addAll(response.body()!!.discovery_answers!!)
                    _sendQuestion.postValue(DataHolder.success(_sendQuestion.value!!.data!!))
                }
            }

        }
    }
}