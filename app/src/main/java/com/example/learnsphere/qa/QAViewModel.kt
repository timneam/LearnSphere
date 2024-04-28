package com.example.learnsphere.qa

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QAViewModel(private val repository: QARepository) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(mutableStateListOf())
    val questions: StateFlow<List<Question>> = _questions

    private val _replies = MutableStateFlow<List<Reply>>(mutableStateListOf())
    val replies: StateFlow<List<Reply>> = _replies

    fun getQuestions(qnaId: String) {
        viewModelScope.launch {
            _questions.value = repository.getQuestions(qnaId)
            Log.d("debug", "vmquestions: ${_questions.value}")
        }
    }

    fun addQuestion(question: Question, qnaId: String) {
        viewModelScope.launch {
            repository.addQuestion(question, qnaId)
        }
    }

    fun getReplies(qnaId: String, questionId: String) {
        viewModelScope.launch {
            _replies.value = repository.getReplies(qnaId, questionId)
            Log.d("debug", "vmreplies: ${_replies.value}")
        }
    }

    fun addReply(reply: Reply, questionId: String, qnaId: String) {
        viewModelScope.launch {
            repository.addReply(reply, questionId, qnaId)
        }
    }
}
