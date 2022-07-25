package com.nimkat.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimkat.app.models.AnswerModel
import com.nimkat.app.models.DataHolder
import com.nimkat.app.repository.SingleQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleQuestionViewModel @Inject constructor(
    private val repository: SingleQuestionRepository
) : ViewModel() {
    private val _answers =
        MutableLiveData<DataHolder<MutableList<AnswerModel>>>(DataHolder.pure())
    val answers: LiveData<DataHolder<MutableList<AnswerModel>>> = _answers


    fun loadAnswers(questionId: Int) {
        _answers.postValue(DataHolder.loading())

        viewModelScope.launch {
            val response = repository.getAnswer(questionId)
            if (response == null || !response.isSuccessful) {
                _answers.postValue(DataHolder.error())
            } else {
                if (response.body() == null || response.body()!!.results.isEmpty()) {
                    Log.d(
                        "SingleQuestionViewModel",
                        "response.body() == null || response.body()!!.results.isEmpty()"
                    )
                    if (_answers.value == null || _answers.value!!.data == null) {
                        _answers.postValue(
                            DataHolder.pure()
                        )
                    } else {
                        _answers.postValue(
                            DataHolder.success(_answers.value!!.data!!)
                        )
                    }
                } else if (_answers.value!!.data == null) {
                    Log.d("SingleQuestionViewModel", "_answers.value!!.data == null")
                    _answers.postValue(DataHolder.success(response.body()!!.results.toMutableList()));
                } else {
                    Log.d("SingleQuestionViewModel", "else")
                    _answers.value!!.data!!.addAll(response.body()!!.results)
                    _answers.postValue(DataHolder.success(_answers.value!!.data!!))
                }
            }
        }
    }
}