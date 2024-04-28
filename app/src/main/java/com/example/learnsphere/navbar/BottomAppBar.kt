package com.example.learnsphere.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.learnsphere.navGraph.Screen

@Composable
fun BottomAppBarImplementation(navController: NavController) {
    BottomAppBar(
        actions = {
            IconButton(
                onClick = { navController.navigate(Screen.HomeScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }
            IconButton(
                onClick = { navController.navigate(Screen.CalendarScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = "Calendar",
                )
            }

            IconButton(
                onClick = { navController.navigate(Screen.AttendanceUserCheckScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Filled.QrCode,
                    contentDescription = "Attendance User Check",
                )
            }

            IconButton(
                onClick = { navController.navigate(Screen.HistoryScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Filled.QuestionAnswer,
                    contentDescription = "Q&A",
                )
            }
            IconButton(
                onClick = { navController.navigate(Screen.ProfileScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profile",
                )
            }
        },
    )
}
// [END android_compose_components_bottomappbar]

@Preview
@Composable
fun BottomAppBarPreview() {
    val navController = rememberNavController()
    BottomAppBarImplementation(navController)
}