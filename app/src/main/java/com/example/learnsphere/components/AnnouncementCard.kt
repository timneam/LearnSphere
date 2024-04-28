package com.example.learnsphere.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun AnnouncementCard(
    moduleName: String,
    date: String,
    message: String,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Column(
            Modifier.padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Module $moduleName",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = "Date $date",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.SemiBold,
                )

            }


            Text(
                text = message,
//                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
//                        "Proin in ex et nulla vehicula sollicitudin." +
//                        "Duis dignissim turpis eu accumsan iaculis." +
//                        "Sed facilisis convallis ullamcorper.",
            )

        }
    }

}