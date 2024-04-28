package com.example.learnsphere.history

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.navbar.BottomAppBarImplementation
//import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class RepliesClass(
    val questionID: String,
//    val question: String,
    val anonymous: Boolean,
    val comment: String,
    val userID: String,
    val username: String
)

@Composable
fun ThreadScreen(
    navController: NavController
) {
    val ids = QuestionIdStack.pop()

    val qnaId = ids?.first.toString()
    val questionId = ids?.second.toString()

    Log.d("ThreadScreen", "qnaId: $qnaId, questionId: $questionId")

//    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var question = remember {
        mutableStateOf("")
    }
    var repliesList = remember {
        mutableListOf<RepliesClass>()
    }

    // Fetch all Replies data from Firestore
    val qnaDocRef = db.collection("qna").document(qnaId)

    qnaDocRef.get().addOnSuccessListener { qnaDoc ->
        if (qnaDoc.exists()) {
            // Access the questions collection within the qna document
            val questionsColRef = qnaDocRef.collection("questions")

            questionsColRef.document(questionId).get().addOnSuccessListener { questionDoc ->
                if (questionDoc.exists()) {
                    question.value = questionDoc.getString("question") ?: ""
                    // Access the replies collection within the question document
                    val repliesColRef = questionsColRef.document(questionId).collection("replies")

                    // Now you can fetch data from the replies collection
                    repliesColRef.get().addOnSuccessListener { repliesSnapshot ->
                        // Process the replies data
                        for (replyDoc in repliesSnapshot) {
                            // Access data in each reply document
                            val anonymous = replyDoc.getBoolean("anonymous") ?: false
                            val comment = replyDoc.getString("comment") ?: ""
                            val userID = replyDoc.getString("userId") ?: ""
                            val username = replyDoc.getString("username") ?: ""
                            repliesList.add(
                                RepliesClass(
                                    questionID = questionId,
//                                    question = questionDoc.getString("question") ?: "",
                                    anonymous = anonymous,
                                    comment = comment,
                                    userID = userID,
                                    username = username
                                )
                            )
                        }
                        Log.d("repliesList", "Replies list: $repliesList")
                    }.addOnFailureListener { e ->
                        // Handle failure to fetch replies collection
                        Log.e("Firestore", "Error getting replies collection", e)
                    }
                } else {
                    // Handle case where question document does not exist
                    Log.e("Firestore", "Question document does not exist")
                }
            }.addOnFailureListener { e ->
                // Handle failure to fetch question document
                Log.e("Firestore", "Error getting question document", e)
            }
        } else {
            // Handle case where qna document does not exist
            Log.e("Firestore", "QNA document does not exist")
        }
    }.addOnFailureListener { e ->
        // Handle failure to fetch qna document
        Log.e("Firestore", "Error getting QNA document", e)
    }

    Scaffold (
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ){it
        Column (
            modifier = Modifier
                .padding(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "${question.value}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(repliesList.size) { index ->
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(10.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
//                        Text(
//                            text = "Question: ${repliesList[index].question}",
//                            fontWeight = FontWeight.Bold
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Comment: ${repliesList[index].comment}"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (repliesList[index].anonymous) {
                                Text(
                                    text = "By Anonymous"
                                )
                            } else {
                                Text(
                                    text = "By username: ${repliesList[index].username}"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}
