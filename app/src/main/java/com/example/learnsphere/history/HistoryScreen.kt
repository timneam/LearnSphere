package com.example.learnsphere.history

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learnsphere.navGraph.Screen
import com.example.learnsphere.navbar.BottomAppBarImplementation
//import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class QNAClass(
    // qnaID is the document ID of the QNA
    val qnaID: String,
    val course: String,
    val modCode: String,
    val questions: List<String>,
    // questionID is the document ID of the question
    val questionID: String,
)

@Composable
fun HistoryScreen(
    navController: NavController,
) {
//    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var qnaList = remember {
        mutableListOf<QNAClass>()
    }
    var uniqueQnaIds = remember {
        HashSet<String>()
    }

    // Fetch all QNA data from Firestore
    db.collection("qna")
        .get()
        .addOnSuccessListener { docs: QuerySnapshot ->
            for (doc in docs) {
                val qnaID = doc.id

                if (uniqueQnaIds.add(qnaID)) {
                    val course = doc.getString("course") ?: ""
                    val modCode = doc.getString("mod_code") ?: ""

                    db.collection("qna").document(qnaID).collection("questions").get()
                        .addOnSuccessListener { questions ->
                            for (question in questions) {
//                        Log.d("History Screen -> question", "question: ${question.data}")
                                val questionID = question.id
                                // add the question to the qnaList
                                qnaList.add(
                                    QNAClass(
                                        qnaID = qnaID,
                                        course = course,
                                        modCode = modCode,
                                        questions = listOf(question.data["question"].toString()),
                                        questionID = questionID
                                    )
                                )
                            }
                            Log.d(
                                "History Screen -> qna",
                                "qna id : $qnaID, course: $course, mod_code: $modCode"
                            )
                            Log.d("History Screen -> qnaList", "qnaList: $qnaList")
                        }.addOnFailureListener { e ->
                        Log.d("History Screen -> question", "Error getting documents: ", e)
                    }
                }

//                if (questionList is List<*>) {
//                    val questions = questionList.filterIsInstance<String>()
//                    Log.d("History Screen -> questions", "questions: $questions")
//                    qnaList.add(QNAClass(course = course, questions = questions, modCode = mod_code, qnaID = qnaID))
//                    Log.d("History Screen -> qnaList", "qnaList: $qnaList")
//                }
            }
        }

    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ) {
        it
        Column(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            // a row for title, qr code, and search
            Row {
                Column {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        text = "History"
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier
                            .padding(25.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Black, // Adjust tint color if needed
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(qnaList.size) { index ->
                    Log.d("qnaSize", "qnaList size: ${qnaList.size}")
                    val qnaItem = qnaList[index]
                    // a row for all forums
                    Row {
                        Column (
                            modifier= Modifier.fillMaxSize()
                        ){
//                            Card(
//                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
//                                modifier = Modifier
//                                    .padding(10.dp)
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        QuestionIdStack.push(qnaItem.qnaID, qnaItem.questionID)
//                                        navController.navigate("${Screen.ThreadScreen.route}")
//                                    },
//
//                                elevation = CardDefaults.cardElevation(10.dp),
//                            ) {
                            Card(
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        QuestionIdStack.push(qnaItem.qnaID, qnaItem.questionID)
                                        navController.navigate("${Screen.ThreadScreen.route}")
                                    },
                                        elevation = CardDefaults.cardElevation(10.dp),
                            ) {
                                Column(
                                    Modifier.padding(20.dp)
                                )
                                {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        softWrap = true,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        text = qnaItem.questions[0]
                                    )

                                    Text(
                                        style = MaterialTheme.typography.bodySmall,
                                        text = "Question ID: ${qnaItem.questionID}"
                                    )

                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "View More >>",
                                        textAlign = TextAlign.Right,
                                        textDecoration = TextDecoration.Underline,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

//                                if (qnaItem.questions.isNotEmpty()) {
//                                    qnaItem.questions.forEach { question ->
//                                        Text(
//                                            modifier = Modifier.padding(8.dp),
//                                            style = MaterialTheme.typography.bodyLarge,
//                                            text = "Question: $question"
//                                        )
//                                    }
//                                } else {
//                                    Text(
//                                        modifier = Modifier.padding(8.dp),
//                                        style = MaterialTheme.typography.bodyLarge,
//                                        text = "No questions"
//                                    )
//
//                                }

                            }
                        }
                    }
                }
            }


        }
    }

}
