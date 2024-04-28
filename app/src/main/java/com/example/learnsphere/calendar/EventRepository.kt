package com.example.learnsphere.calendar

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

class EventRepository() {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("calendar")

    // Assume this function retrieves event data from Firebase based on the event ID
    suspend fun getEventById(eventId: String): Event {
        val document = collection.document(eventId).get().await()
        val data = document.data
        return Event(
            id = eventId,
            eventTitle = data?.get("event_title") as String,
            eventRemarks = data["event_remarks"] as String,
            modId = data["mod_id"] as String,
            eventDateTime = data["event_date_time"] as Long
        )
    }

    suspend fun getEventsForMonth(yearMonth: YearMonth): List<Event> {
        val startOfDayMillis = yearMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endOfDayMillis = yearMonth.atEndOfMonth().atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli()

        val querySnapshot = collection
            .whereGreaterThanOrEqualTo("event_date_time", startOfDayMillis)
            .whereLessThanOrEqualTo("event_date_time", endOfDayMillis)
            .get()
            .await()

        return querySnapshot.documents.map { document ->
            Event(
                id = document.id,
                eventTitle = document["event_title"] as String,
                eventRemarks = document["event_remarks"] as String,
                modId = document["mod_id"] as String,
                eventDateTime = document["event_date_time"] as Long
            )
        }.sortedBy { it.eventDateTime }
    }

    suspend fun addOrUpdateEvent(event: Event) {
        val eventMap = mapOf(
            "event_title" to event.eventTitle,
            "event_remarks" to event.eventRemarks,
            "mod_id" to event.modId,
            "event_date_time" to event.eventDateTime
        )

        if (event.id.isEmpty()) {
            collection.add(eventMap).await()
        } else {
            collection.document(event.id).set(eventMap).await()
        }
    }

    suspend fun deleteEvent(eventId: String) {
        Log.d("debug", "delevent: ${eventId}")
        collection.document(eventId).delete().await()
    }

    fun getEventsForDate(date: LocalDate, listener: (List<Event>) -> Unit) {
        val startOfDayMillis = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endOfDayMillis = date.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli()

        collection
            .whereGreaterThanOrEqualTo("event_date_time", startOfDayMillis)
            .whereLessThanOrEqualTo("event_date_time", endOfDayMillis)
            .get()
            .addOnSuccessListener { result ->
                val events = mutableListOf<Event>()
                for (document in result) {
                    val event = Event(
                        id = document.id,
                        eventTitle = document["event_title"] as String,
                        eventRemarks = document["event_remarks"] as String,
                        modId = document["mod_id"] as String,
                        eventDateTime = document["event_date_time"] as Long
                    )
                    events.add(event)
                }
                listener(events)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                listener(emptyList())
            }
    }
}
