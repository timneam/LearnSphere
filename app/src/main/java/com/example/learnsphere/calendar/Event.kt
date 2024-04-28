package com.example.learnsphere.calendar

data class Event(
    val id: String,
    val eventTitle: String,
    val eventRemarks: String,
    val modId: String,
    val eventDateTime: Long
)