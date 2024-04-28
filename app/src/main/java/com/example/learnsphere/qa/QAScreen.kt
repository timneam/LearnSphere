package com.example.learnsphere.qa

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.learnsphere.home.CoursesList
import com.example.learnsphere.home.ModuleClass
import com.example.learnsphere.navbar.BottomAppBarImplementation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun QAScreen(
    navController: NavController,
    viewModel: QAViewModel,
    qnaId: String?
) {
    val db = Firebase.firestore
    var modCode by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    viewModel.getQuestions(qnaId.toString())
    val questionsState by viewModel.questions.collectAsState()
    var questionText by remember { mutableStateOf("") }
    var questionID by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    val coroutineScope = rememberCoroutineScope()
//    val questionsFlow = MutableStateFlow(Unit) // Create a flow to trigger the fetching of questions

// Use LaunchedEffect to start collecting the flow
    LaunchedEffect(questionsState) {
        coroutineScope.launch {
            while (true) {
                Log.d("debug", "get questions")
                viewModel.getQuestions(qnaId.toString())
                delay(2000) // Delay for 2 seconds
            }
        }
    }

    LaunchedEffect(Firebase.auth.currentUser) {
        val currentUser = Firebase.auth.currentUser
        val currentId = currentUser?.uid
        Log.d("debug", "current id: ${currentId}")
        val qna = db.collection("qna").document(qnaId.toString()).get().await()
        modCode = qna.data?.get("mod_code") as String
        val userDoc = db.collection("users").document(currentId.toString()).get().await()
        Log.d("debug", "user: ${userDoc}")
        username = userDoc.data?.get("userName") as String
    }

    if (showDialog) { // using dialog for now
        RepliesDialog(
            onClose = { showDialog = false },
            viewModel = viewModel,
            questionId = questionID,
            qnaId = qnaId.toString(),
            username = username
        )
    }

    // Scaffold and other UI code...
    Scaffold(
        bottomBar = {
            BottomAppBarImplementation(navController = navController)
        }
    ) {it
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = modCode,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.padding(5.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(questionsState) {question ->
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Column(
                            Modifier
                                .padding(10.dp)
                                .clickable {
                                    questionID = question.id.toString()
                                    showDialog = true
                                }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxWidth()
                            ) {
//                                Text(
//                                    text = question.userId,
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
                                Text(
                                    text = question.question,
                                    modifier = Modifier
                                        .weight(2f)
                                )
                            }
                        }
                    }
                }
            }

            // Text field and icon button at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            // Handle sending the question
                            if (userId != null && questionText.isNotBlank()) {
                                val newQuestion = Question(userId = userId, isAnonymous = true, question = questionText, replies = emptyList())
                                viewModel.addQuestion(newQuestion, qnaId.toString())
                                viewModel.getQuestions(qnaId.toString())
                                questionText = ""
                            }
                        }
                    ),
                    placeholder = {
                        Text(text = "Enter question")
                    }
                )
                IconButton(onClick = {
                    // Handle sending the question
                    if (userId != null && questionText.isNotBlank()) {
                        val newQuestion = Question(userId = userId, isAnonymous = false, question = questionText, replies = emptyList())
                        viewModel.addQuestion(newQuestion, qnaId.toString())
                        viewModel.getQuestions(qnaId.toString())
                        questionText = ""
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}


@Composable
fun RepliesDialog(
    onClose: () -> Unit,
    viewModel: QAViewModel,
    questionId: String,
    qnaId: String,
    username: String
) {
    val coroutineScope = rememberCoroutineScope()
    viewModel.getReplies(qnaId, questionId)
    val repliesState by viewModel.replies.collectAsState()
    var replyText by remember { mutableStateOf("") }
    LaunchedEffect(repliesState) {
        coroutineScope.launch {
            while (true) {
                Log.d("debug", "get replies")
                viewModel.getReplies(qnaId, questionId)
                delay(2000) // Delay for 2 seconds
            }
        }
    }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.padding(end = 8.dp) // Adjust padding as needed
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Text(
                textAlign = TextAlign.Center,
                text = "Replies",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.padding(5.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(repliesState) { reply ->
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Column(
                            Modifier.padding(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxWidth()
                            ) {
//git

                                Text(
                                    text = reply.comment,
                                    modifier = Modifier
                                        .weight(2f)
                                )
                            }
                        }
                    }
                }
            }

            // Text field and icon button at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            // Handle sending the question
                            val user = FirebaseAuth.getInstance().currentUser
                            val userId = user?.uid
                            if (userId != null && replyText.isNotBlank()) {
                                val newReply = Reply(userId, username, false, replyText)
                                viewModel.addReply(newReply, questionId, qnaId)
                                viewModel.getReplies(qnaId, questionId)
                                replyText = "" // Clear the text field after sending
                            }
                        }
                    ),
                    placeholder = {
                        Text(text = "Enter reply")
                    }
                )
                IconButton(onClick = {
                    // Handle sending the question
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid
                    if (userId != null && replyText.isNotBlank()) {
                        val newReply = Reply(userId, username, false, replyText)
                        viewModel.addReply(newReply, questionId, qnaId)
                        viewModel.getReplies(qnaId, questionId)
                        replyText = "" // Clear the text field after sending
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
    }
}