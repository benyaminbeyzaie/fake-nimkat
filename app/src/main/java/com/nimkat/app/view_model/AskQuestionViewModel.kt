package com.nimkat.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimkat.app.models.DataHolder
import com.nimkat.app.models.DiscoveryAnswers
import com.nimkat.app.models.QuestionModel
import com.nimkat.app.repository.AskQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskQuestionViewModel @Inject constructor(
    private val repository: AskQuestionRepository,
) : ViewModel() {
    private var _questionId = MutableLiveData<DataHolder<Int>>(DataHolder.pure())
    var questionId: LiveData<DataHolder<Int>> = _questionId

    private var _askedTeacher = MutableLiveData<DataHolder<Boolean>>(DataHolder.pure())
    var askedTeacher: LiveData<DataHolder<Boolean>> = _askedTeacher

    private var _discoveryAnswers =
        MutableLiveData<DataHolder<ArrayList<DiscoveryAnswers>>>(DataHolder.pure())
    var discoveryAnswers: LiveData<DataHolder<ArrayList<DiscoveryAnswers>>> = _discoveryAnswers

    private var _questionModel =
        MutableLiveData<DataHolder<QuestionModel>>(DataHolder.pure())
    var questionModel: LiveData<DataHolder<QuestionModel>> = _questionModel

    fun sendQuestion(question: String) {
        _discoveryAnswers.postValue(DataHolder.loading())

        viewModelScope.launch {
            if (repository.isAuth() == null) {
                _discoveryAnswers.postValue(DataHolder.needLogin())
                return@launch
            }

            val response = repository.sendTextQuestion(question)
            if (response == null || !response.isSuccessful) {
                _discoveryAnswers.postValue(DataHolder.error())
                _questionModel.postValue(DataHolder.error())
            } else {
                _questionId.postValue(DataHolder.success(response.body()!!.id!!))
                _discoveryAnswers.postValue(DataHolder.success(response.body()!!.discoveryAnswers))
                _questionModel.postValue(DataHolder.success(response.body()!!))
            }

        }
    }

    fun askTeachers(questionId: String) {
        _askedTeacher.postValue(DataHolder.loading())

        viewModelScope.launch {
            if (repository.isAuth() == null) {
                _discoveryAnswers.postValue(DataHolder.needLogin())
                return@launch
            }

            val response = repository.askTeachers(questionId = questionId)
            if (response == null || !response.isSuccessful) {
                _askedTeacher.postValue(DataHolder.error())
            } else {
                _questionId.postValue(DataHolder.success(response.body()!!.id!!))
                _questionModel.postValue(DataHolder.success(response.body()!!))
                _askedTeacher.postValue(DataHolder.success(true))
            }
        }
    }

    fun askImageQuestion(base64: String) {
        _discoveryAnswers.postValue(DataHolder.loading())

        viewModelScope.launch {
            try {
                if (repository.isAuth() == null) {
                    _discoveryAnswers.postValue(DataHolder.needLogin())
                    return@launch
                }

                val response = repository.sendImageQuestion(base64)
                if (response == null || !response.isSuccessful) {
                    _discoveryAnswers.postValue(DataHolder.error())
                    _questionModel.postValue(DataHolder.error())
                } else {
                    _questionId.postValue(DataHolder.success(response.body()!!.id!!))
                    _discoveryAnswers.postValue(DataHolder.success(response.body()!!.discoveryAnswers))
                    _questionModel.postValue(DataHolder.success(response.body()!!))
                }
            }catch (e: Exception){
                _discoveryAnswers.postValue(DataHolder.error())
                _questionModel.postValue(DataHolder.error())
            }

        }
    }
}