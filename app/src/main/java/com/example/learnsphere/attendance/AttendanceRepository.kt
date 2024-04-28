package com.example.learnsphere.attendance

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AttendanceRepository(private val db: FirebaseFirestore) {

//    fun startAttendance(callback: (Boolean, String, String?) -> Unit) {
//        val qnaId = UUID.randomUUID().toString()
//        val roomData = hashMapOf(
//            "date_created" to System.currentTimeMillis().toString(),
//            "questions" to emptyList<String>()
//        )
//
//        db.collection("qna").document(qnaId).set(roomData)
//            .addOnSuccessListener {
//                callback(true, "QnA room created successfully.", qnaId)
//            }
//            .addOnFailureListener { e ->
//                callback(false, "Failed to create QnA room: ${e.message}", null)
//            }
//    }

    suspend fun startAttendance(modCode: String, courseId: String, callback: (Boolean, String, String?) -> Unit) {
        val course = db.collection("courses").document(courseId).get().await()
        val courseCode = course.data?.get("course_code") as String
        val roomData = hashMapOf(
            "course" to courseCode,
            "mod_code" to modCode
        )

        // Add a new document with Firestore-generated ID
        db.collection("qna").add(roomData)
            .addOnSuccessListener { documentReference ->
                // documentReference.getId() will give you the Firestore generated ID
                val qnaId = documentReference.id
                callback(true, "QnA room created successfully.", qnaId)
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to create QnA room: ${e.message}", null)
            }
    }

    fun nfcScanStore(qnaId: String, instructorId: String, venueId: String, callback: (Boolean, String?) -> Unit) {
        val attendanceData = hashMapOf(
            "date_time" to System.currentTimeMillis(),
            "qna_id" to qnaId,
            "instructor" to instructorId,
            "venue" to venueId,
            "head_count" to listOf<String>() // Empty list for head_count
        )

        db.collection("attendance").add(attendanceData)
            .addOnSuccessListener { documentReference ->
                callback(true, documentReference.id) // Pass back the document ID upon success
            }
            .addOnFailureListener { e ->
                callback(false, e.message) // Pass back the error message on failure
            }
    }

    fun addToHeadCount(attendanceId: String, userId: String, userLatitude: Double, userLongitude: Double, callback: (Boolean, String?, String?) -> Unit) {
        Log.d("addToHeadCount", "Adding user to head count")
        val attendanceRef = db.collection("attendance").document(attendanceId)

        db.runTransaction { transaction ->
            // Retrieve the attendance document
            val attendance = transaction.get(attendanceRef)
            val venueId = attendance.getString("venue") ?: return@runTransaction null // Early return if no venue ID
            Log.d("venueId", venueId)
            val venueRef = db.collection("venue").document(venueId)
            Log.d("venueRef", venueRef.path)
            val venue = transaction.get(venueRef)
            Log.d("venue", venue.toString())
            val venueLatLng = venue.getGeoPoint("latlng") ?: return@runTransaction null // Early return if no latlng
            Log.d("venueLocation", "Latitude: ${venueLatLng.latitude}, Longitude: ${venueLatLng.longitude}")

            // Compare user's location to the venue's location
            if (!isUserInVicinity(userLatitude, userLongitude, venueLatLng.latitude, venueLatLng.longitude)) {
                // If the user is not in vicinity, do not proceed with adding to headcount
                return@runTransaction "User not in vicinity"
            }

            // Proceed to update headcount
            val currentHeadCount = attendance.get("head_count") as List<String>? ?: emptyList()
            val qnaId = attendance.getString("qna_id") // Get the qnaId from the document

            if (userId !in currentHeadCount) {
                transaction.update(attendanceRef, "head_count", currentHeadCount + userId)
            }

            qnaId // This is returned to the onSuccessListener
        }.addOnSuccessListener { qnaId ->
            // qnaId is the one returned from the transaction block
            callback(true, qnaId, "User added to head count successfully.")
        }.addOnFailureListener { e ->
            callback(false, null, e.localizedMessage)
        }
    }

    // Utility function to check if the user is within the vicinity of the venue
    private fun isUserInVicinity(userLat: Double, userLong: Double, venueLat: Double, venueLong: Double): Boolean {
        // using Haversine formula
        val earthRadius = 6371 // Radius of the earth in kilometers
        val latDistance = Math.toRadians(venueLat - userLat)
        val lonDistance = Math.toRadians(venueLong - userLong)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(venueLat)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c // Convert to distance in kilometers

        val vicinityRadius = 0.5 // Define vicinity radius in kilometers
        return distance <= vicinityRadius
    }
}
