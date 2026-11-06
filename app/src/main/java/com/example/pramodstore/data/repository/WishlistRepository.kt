package com.example.pramodstore.data.repository

import android.content.Context
import com.example.pramodstore.data.model.Product
import com.example.pramodstore.data.model.WishlistItem
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

class WishlistRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(
                context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            )
        )
    )

    private fun userWishlistCollection(userId: String) =
        db.collection("users").document(userId).collection("wishlist")

    fun observeWishlist(userId: String): Flow<List<WishlistItem>> = flow {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || userId.isBlank() || currentUser.uid != userId) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/wishlist"
        userWishlistCollection(userId).snapshots()
            .map { snapshot -> snapshot.toObjects(WishlistItem::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun toggleWishlist(userId: String, product: Product): Result<Boolean> = try {
        require(userId.isNotBlank()) { "User ID required" }
        val docRef = userWishlistCollection(userId).document(product.id)
        val existing = docRef.get().await()

        if (existing.exists()) {
            docRef.delete().await()
            Result.success(false) // removed
        } else {
            val itemData = hashMapOf<String, Any?>(
                "userId" to userId,
                "productId" to product.id,
                "productName" to product.name,
                "productPrice" to product.price,
                "productImageUrl" to product.imageUrl,
                "createdAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(itemData).await()
            Result.success(true) // added
        }
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "users/$userId/wishlist/${product.id}")
        Result.failure(e)
    }
}
