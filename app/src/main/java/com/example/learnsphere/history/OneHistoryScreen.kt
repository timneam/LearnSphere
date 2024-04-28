package com.example.learnsphere.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun OneHistoryScreen(
    navController: NavController
) {
    Column {
        Text(
            text = "One History Screen",
            modifier = Modifier
                .padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        getHistoryBasedOnID()
    }
}

// This function is used to get one qna based on document id
// and get all the "questions" related to that document id
@Composable
fun getHistoryBasedOnID() {
    // Fetch history based on document ID

    chat(modifier = Modifier)

}

// This function is the design of the chat
@Composable
fun chat(
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(30) { index ->
            Row {
                Card (
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(100.dp)
                ) {
                    Column (
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Question $index")
                        Text(text = "User ID: $index")
                    }
                }
            }
        }
    }
}