package com.nimkat.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimkat.app.models.DataHolder
import com.nimkat.app.models.QuestionModel
import com.nimkat.app.repository.MyQuestionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyQuestionsViewModel @Inject constructor(
    private val repository: MyQuestionsRepository,
) : ViewModel() {
    private val _myQuestions =
        MutableLiveData<DataHolder<MutableList<QuestionModel>>>(DataHolder.pure())
    val myQuestions: LiveData<DataHolder<MutableList<QuestionModel>>> = _myQuestions

    private var page = 0
    private var hasMore = true

    fun loadQuestions() {
        page++
        if (!hasMore) return
        if (page > 1) _myQuestions.postValue(DataHolder.loadingNext(_myQuestions.value?.data))
        else _myQuestions.postValue(DataHolder.loading())

        viewModelScope.launch {
            val response = repository.getQuestions(page = page)
            if (response == null || !response.isSuccessful) {
                _myQuestions.postValue(DataHolder.error())
            } else {
                if (response.body() == null || response.body()!!.results.isEmpty()) {
                    Log.d(
                        "My Question View Model",
                        "response.body() == null || response.body()!!.results.isEmpty()"
                    )
                    hasMore = false;
                    if (_myQuestions.value == null || _myQuestions.value!!.data == null) {
                        _myQuestions.postValue(
                            DataHolder.pure()
                        )
                    } else {
                        _myQuestions.postValue(
                            DataHolder.success(_myQuestions.value!!.data!!)
                        )
                    }
                } else if (_myQuestions.value!!.data == null) {
                    Log.d("My Question View Model", "_myQuestions.value!!.data == null")
                    _myQuestions.postValue(DataHolder.success(response.body()!!.results.toMutableList()));
                } else {
                    Log.d("My Question View Model", "else")
                    _myQuestions.value!!.data!!.addAll(response.body()!!.results)
                    _myQuestions.postValue(DataHolder.success(_myQuestions.value!!.data!!))
                }
            }

        }
    }

}