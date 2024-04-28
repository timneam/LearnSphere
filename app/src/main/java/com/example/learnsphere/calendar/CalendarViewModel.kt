package com.example.learnsphere.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

class CalendarViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _eventsListState = MutableStateFlow<List<Event>>(emptyList())
    val eventsListState: StateFlow<List<Event>> = _eventsListState

    private val _eventState = MutableStateFlow<Event?>(null)
    val eventState: StateFlow<Event?> = _eventState

    fun getEventsForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val events = eventRepository.getEventsForMonth(yearMonth)
            _eventsListState.value = events
            Log.d("ViewModel", "ViewModel: ${_eventsListState.value}")
        }
    }

    fun getEventById(eventId: String) {
        viewModelScope.launch {
            val event = eventRepository.getEventById(eventId)
            _eventState.value = event
        }
    }

    fun addOrUpdateEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.addOrUpdateEvent(event)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
        }
    }
}