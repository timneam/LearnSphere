package com.example.learnsphere.home

import com.google.firebase.firestore.FirebaseFirestore

class AnnouncementRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addAnnouncement(announcement: com.example.learnsphere.home.AnnouncementClass, courseId: String, moduleId: String) {
        try {
            val announcementCollection = firestore.collection("courses").document(courseId).collection("modules").document(moduleId).collection("announcements")
            announcementCollection.add(announcement)
        } catch (e: Exception) {
            println("Add Announcement in Announcement Repository: ${e.message}")
        }
    }
}
