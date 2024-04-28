package com.example.learnsphere.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnnouncementViewModel(
    private val repository: AnnouncementRepository
) : ViewModel() {

        private val _announcements = MutableStateFlow<List<AnnouncementClass>>(mutableStateListOf())
        val announcements: StateFlow<List<AnnouncementClass>> = _announcements

        fun addAnnouncement(announcement: AnnouncementClass, courseId: String, moduleId: String) {
            viewModelScope.launch {
                repository.addAnnouncement(announcement, courseId, moduleId)
            }
        }
}
