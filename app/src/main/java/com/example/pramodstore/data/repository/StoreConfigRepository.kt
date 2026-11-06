package com.example.pramodstore.data.repository

import android.content.Context
import com.example.pramodstore.data.model.Inquiry
import com.example.pramodstore.data.model.StoreConfig
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

class StoreConfigRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(
                context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            )
        )
    )

    private val settingsDoc = db.collection("settings").document("store_config")
    private val inquiriesCollection = db.collection("inquiries")

    val defaultStoreConfig = StoreConfig(
        id = "store_config",
        supportEmail = "rohanguptamorwabazar@gmail.com",
        supportPhone = "",
        storeAddress = "PRAMOD BOOK STORE, Main Road, Morwa Bazar, Samastipur, Bihar - 848121",
        storeHours = "Monday to Sunday: 9:00 AM - 9:30 PM",
        aboutText = "",
        termsText = "",
        privacyText = ""
    )

    fun observeStoreConfig(): Flow<StoreConfig> = flow {
        settingsDoc.snapshots()
            .map { snapshot ->
                snapshot.toObject(StoreConfig::class.java) ?: defaultStoreConfig
            }
            .catch {
                emit(defaultStoreConfig)
            }
            .collect { emit(it) }
    }

    suspend fun saveStoreConfig(config: StoreConfig): Result<Unit> = try {
        val data = hashMapOf<String, Any?>(
            "supportEmail" to config.supportEmail.trim(),
            "supportPhone" to config.supportPhone.trim(),
            "storeAddress" to config.storeAddress.trim(),
            "storeHours" to config.storeHours.trim(),
            "aboutText" to config.aboutText.trim(),
            "termsText" to config.termsText.trim(),
            "privacyText" to config.privacyText.trim(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        settingsDoc.set(data).await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "settings/store_config")
        Result.failure(e)
    }

    suspend fun submitInquiry(inquiry: Inquiry): Result<String> = try {
        val docRef = inquiriesCollection.document()
        val data = hashMapOf<String, Any?>(
            "name" to inquiry.name.trim(),
            "email" to inquiry.email.trim(),
            "phone" to inquiry.phone.trim(),
            "subject" to inquiry.subject.trim(),
            "message" to inquiry.message.trim(),
            "createdAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        docRef.set(data).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "inquiries")
        Result.failure(e)
    }

    fun observeInquiries(): Flow<List<Inquiry>> = flow {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || currentUser.email != "hhahahhah552@gmail.com") {
            emit(emptyList())
            return@flow
        }
        inquiriesCollection.snapshots()
            .map { snapshot ->
                snapshot.toObjects(Inquiry::class.java).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "inquiries")
                emit(emptyList())
            }
            .collect { emit(it) }
    }
}
