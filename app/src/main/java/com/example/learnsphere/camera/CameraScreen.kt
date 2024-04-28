package com.example.learnsphere.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview (showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CameraScreen() {
    Column (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            text = "Camera"
        )

        Card (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Camera Feature"
            )
        }
    }
}