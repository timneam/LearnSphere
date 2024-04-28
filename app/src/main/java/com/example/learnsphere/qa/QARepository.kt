package com.example.learnsphere.qa

import android.util.Log
import com.example.learnsphere.home.ModuleClass
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QARepository {

    private val firestore = FirebaseFirestore.getInstance()
    suspend fun getQna(qnaId: String): List<Question> {
        val qnaDoc = firestore.collection("qna").document(qnaId).get().await()
        val questionsCollection = qnaDoc.reference.collection("questions").get().await()
        val questions = mutableListOf<Question>()
        for (question in questionsCollection) {
//            val question = question.toObject(Question::class.java)
            val qnsid = question.id
            val qnsuserid = question.data["userId"].toString()
            val qnsanon = question.data["anonymous"] as Boolean
            val qns = question.data["question"].toString()
            val question = Question(id = qnsid, userId = qnsuserid, isAnonymous = qnsanon, question = qns, replies = emptyList())
            question.let { questions.add(it) }
        }
        Log.d("debug", "repoquestions: $questions")
        return questions
    }
    suspend fun getQuestions(qnaId: String): List<Question> {
        val qnaDoc = firestore.collection("qna").document(qnaId).get().await()
        val questionsCollection = qnaDoc.reference.collection("questions").get().await()
        val questions = mutableListOf<Question>()
        for (question in questionsCollection) {
//            val question = question.toObject(Question::class.java)
            val qnsid = question.id
            val qnsuserid = question.data["userId"].toString()
            val qnsanon = question.data["anonymous"] as Boolean
            val qns = question.data["question"].toString()
            val question = Question(id = qnsid, userId = qnsuserid, isAnonymous = qnsanon, question = qns, replies = emptyList())
            question.let { questions.add(it) }
        }
        Log.d("debug", "repoquestions: $questions")
        return questions
    }

    suspend fun addQuestion(question: Question, qnaId: String) {
        try {
            val questionCollection = firestore.collection("qna").document(qnaId).collection("questions")
            questionCollection.add(question)
        } catch (e: Exception) {
            Log.d("ViewModel", "addQuestion: ${e.message}")
        }
    }

    suspend fun getReplies(qnaId: String, questionId: String): List<Reply> {
        val qnaDoc = firestore.collection("qna").document(qnaId).get().await()
        val questionDoc = qnaDoc.reference.collection("questions").document(questionId).get().await()
        val repliesCollection = questionDoc.reference.collection("replies").get().await()
        val replies = mutableListOf<Reply>()
        for (reply in repliesCollection) {
            val reply = reply.toObject(Reply::class.java)
            reply.let { replies.add(it) }
        }
        Log.d("debug", "reporeplies: $replies")
        return replies
    }

    suspend fun addReply(reply: Reply, questionId: String, qnaId: String) {
        try {
            val repliesCollection = firestore.collection("qna").document(qnaId).collection("questions").document(questionId).collection("replies")
            repliesCollection.add(reply)
        } catch (e: Exception) {
            Log.d("ViewModel", "addReply Error: ${e.message}")
        }
    }
}