package com.example.pramodstore.data.repository

import android.content.Context
import com.example.pramodstore.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(
                context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            )
        )
    )

    private val usersCollection = db.collection("users")
    private val adminsCollection = db.collection("admins")

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || userId.isBlank() || (currentUser.uid != userId && currentUser.email != "hhahahhah552@gmail.com")) {
            emit(null)
            return@flow
        }
        usersCollection.document(userId).snapshots()
            .map { it.toObject(UserProfile::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.READ, "users/$userId")
                emit(null)
            }
            .collect { emit(it) }
    }

    suspend fun saveProfile(profile: UserProfile): Result<Unit> = try {
        val docRef = usersCollection.document(profile.userId)
        val data = hashMapOf<String, Any?>(
            "userId" to profile.userId,
            "name" to profile.name,
            "email" to profile.email,
            "phone" to profile.phone,
            "address" to profile.address,
            "city" to profile.city,
            "postalCode" to profile.postalCode,
            "role" to profile.role,
            "createdAt" to (profile.createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        docRef.set(data).await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "users/${profile.userId}")
        Result.failure(e)
    }

    suspend fun checkIsAdmin(userId: String, email: String?): Boolean {
        if (email == "hhahahhah552@gmail.com") return true
        return try {
            val adminDoc = adminsCollection.document(userId).get().await()
            adminDoc.exists()
        } catch (e: Exception) {
            false
        }
    }
}
