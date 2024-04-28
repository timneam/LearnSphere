package com.example.learnsphere.qa

data class Question(
    val id: String? = null,
    val userId: String = "",
    val isAnonymous: Boolean = true,
    val question: String = "",
    val replies: List<String> = emptyList()
)