package com.example.learnsphere.history

// QuestionIdStack.kt

object QuestionIdStack {
    private val stack = mutableListOf<Pair<String, String>>()

    fun push(qnaId: String, questionId: String) {
        stack.add(qnaId to questionId)
    }

    fun pop(): Pair<String, String>? {
        return if (stack.isNotEmpty()) {
            stack.removeAt(stack.size - 1)
        } else {
            null
        }
    }

    fun peek(): Pair<String, String>? {
        return stack.lastOrNull()
    }
}