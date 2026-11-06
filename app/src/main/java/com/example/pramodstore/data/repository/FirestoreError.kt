package com.example.pramodstore.data.repository

import android.util.Log

enum class OperationType {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LIST
}

fun handleFirestoreError(exception: Exception, operation: OperationType, path: String) {
    if (exception is java.util.concurrent.CancellationException || exception is kotlinx.coroutines.CancellationException) {
        return
    }
    val errorData = mapOf(
        "error" to (exception.localizedMessage ?: "Unknown Firestore error"),
        "operation" to operation.name,
        "path" to path,
        "type" to exception.javaClass.simpleName
    )
    Log.e("FirestoreError", "Firestore error occurred: $errorData", exception)
}
