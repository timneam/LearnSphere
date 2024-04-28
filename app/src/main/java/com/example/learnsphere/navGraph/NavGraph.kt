package com.example.learnsphere.navGraph

import AttendanceViewModel
import GenerateQRScreen
import ScanAttendanceScreen
import ScanNFCScreen
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.learnsphere.HomeScreen
import com.example.learnsphere.LoginScreen
import com.example.learnsphere.attendance.AttendanceStartScreen
import com.example.learnsphere.attendance.AttendanceUserCheck
import com.example.learnsphere.calendar.CalendarScreen
import com.example.learnsphere.calendar.CalendarViewModel
import com.example.learnsphere.calendar.EventRepository
import com.example.learnsphere.calendar.EventScreen
import com.example.learnsphere.history.HistoryScreen
import com.example.learnsphere.history.OneHistoryScreen
import com.example.learnsphere.history.ThreadScreen
import com.example.learnsphere.home.AddAnnouncementScreen
import com.example.learnsphere.login.ResetPasswordScreen
import com.example.learnsphere.map.MapScreen
import com.example.learnsphere.profile.ProfileScreen
import com.example.learnsphere.profile.UpdatePFPScreen
import com.example.learnsphere.profile.UpdatePasswordScreen
import com.example.learnsphere.qa.QARepository
import com.example.learnsphere.qa.QAScreen
import com.example.learnsphere.qa.QAViewModel
import com.example.learnsphere.settings.SettingScreen
import com.example.learnsphere.signUp.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route:String){
    object SignUpScreen: Screen(route = "sign_up_screen")
    object LoginScreen: Screen(route = "login_screen")
    object HomeScreen: Screen(route = "home_screen")
    object MapScreen: Screen(route = "map_screen")
    object CalendarScreen: Screen(route = "calendar_screen")
    object AttendanceStartScreen: Screen(route = "attendance_start_screen")
    object AttendanceUserCheckScreen: Screen(route = "attendance_user_check_screen")
    object ScanNFCScreen: Screen(route = "scan_nfc_screen")
    object EventScreen: Screen(route = "event_screen")


    object HistoryScreen: Screen(route = "history_screen")

    object ProfileScreen: Screen(route = "profile_screen")

    object SettingScreen: Screen(route = "setting_screen")
    object GenerateQRScreen: Screen(route = "generate_qr_screen")

    object ScanAttendanceScreen: Screen(route = "scan_attendance_screen")

    object QAScreen: Screen(route = "qa_screen")

    object UpdatePFPScreen: Screen(route = "update_pfp_screen")

    object UpdatePasswordScreen: Screen(route = "update_password_screen")

    object ResetPasswordScreen: Screen(route = "reset_password_screen")

    object OneHistoryScreen: Screen(route = "one_history_screen")

    object AddAnnouncementScreen: Screen(route = "add_announcement_screen")

    object ThreadScreen: Screen(route = "thread/{qnaID}") // route = "thread_screen"
}

@Composable
fun NavGraph(
    navController: NavHostController,
) {

    val context = LocalContext.current
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null)
        Screen.HomeScreen.route
    else
        Screen.LoginScreen.route

    var attendanceViewModel: AttendanceViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable(Screen.HomeScreen.route){
            HomeScreen(
                navController = navController
            )
        }

        composable(Screen.LoginScreen.route){
            LoginScreen(
                navController = navController
            )
        }

        composable(Screen.SignUpScreen.route){
            SignUpScreen(
                navController = navController
            )
        }

//        For Map
        composable(Screen.MapScreen.route){
            MapScreen(context = context)
        }


        composable(Screen.CalendarScreen.route){
            CalendarScreen(
                navController = navController,
                viewModel = CalendarViewModel(eventRepository = EventRepository())
            )
        }

        composable(Screen.AttendanceStartScreen.route){
            AttendanceStartScreen(
                navController = navController,
                attendanceViewModel = attendanceViewModel
            )
        }

        composable(Screen.ScanNFCScreen.route){
            ScanNFCScreen(
                navController = navController,
                attendanceViewModel = attendanceViewModel
            )
        }

        composable(
            Screen.EventScreen.route + "?eventId={eventId}",
            arguments = listOf(navArgument("eventId") { defaultValue = "" })){ backStackEntry ->
            EventScreen(
                navController = navController,
                repository = EventRepository(),
                backStackEntry.arguments?.getString("eventId")
            )
        }

        composable(Screen.HistoryScreen.route) {
            HistoryScreen(
                navController = navController
            )
        }

        composable(Screen.ScanAttendanceScreen.route){
            ScanAttendanceScreen(
                navController = navController,
                attendanceViewModel = attendanceViewModel
            )
        }

        composable(Screen.GenerateQRScreen.route){
            GenerateQRScreen(
                navController = navController,
                attendanceViewModel = attendanceViewModel
            )
        }

        composable(Screen.ProfileScreen.route) {
            ProfileScreen(
                navController = navController
            )
        }

        composable(Screen.SettingScreen.route) {
            SettingScreen(
                navController = navController
            )
        }

        composable(
            Screen.QAScreen.route + "?qnaId={qnaId}",
            arguments = listOf(navArgument("qnaId") { defaultValue = "" })){ backStackEntry ->
            QAScreen(
                navController = navController,
                viewModel = QAViewModel(repository = QARepository()),
                qnaId = backStackEntry.arguments?.getString("qnaId")
            )
        }

        composable(Screen.UpdatePFPScreen.route) {
            UpdatePFPScreen(
                navController = navController
            )
        }

        composable(Screen.UpdatePasswordScreen.route) {
            UpdatePasswordScreen(
                navController = navController
            )
        }

        composable(Screen.ResetPasswordScreen.route) {
            ResetPasswordScreen(
                navController = navController
            )
        }

        composable(Screen.OneHistoryScreen.route) {
            OneHistoryScreen(
                navController = navController
            )
        }

        composable(Screen.AttendanceUserCheckScreen.route) {
            AttendanceUserCheck(
                navController = navController
            )
        }

        composable(Screen.AddAnnouncementScreen.route) {
            AddAnnouncementScreen(
                navController = navController,
            )
        }

        composable(Screen.ThreadScreen.route) {
            ThreadScreen(
                navController = navController,
            )
        }
    }
}
