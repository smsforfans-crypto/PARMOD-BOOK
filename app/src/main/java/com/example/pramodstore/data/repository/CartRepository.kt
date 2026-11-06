package com.example.pramodstore.data.repository

import android.content.Context
import com.example.pramodstore.data.model.CartItem
import com.example.pramodstore.data.model.Product
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

class CartRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(
                context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            )
        )
    )

    private fun userCartCollection(userId: String) =
        db.collection("users").document(userId).collection("cart")

    fun observeCart(userId: String): Flow<List<CartItem>> = flow {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || userId.isBlank() || currentUser.uid != userId) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/cart"
        userCartCollection(userId).snapshots()
            .map { snapshot -> snapshot.toObjects(CartItem::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun addToCart(userId: String, product: Product, quantityToAdd: Int = 1): Result<Unit> = try {
        require(userId.isNotBlank()) { "User ID required" }
        val cartCol = userCartCollection(userId)
        val existingDoc = cartCol.document(product.id).get().await()

        if (existingDoc.exists()) {
            val currentQty = (existingDoc.getLong("quantity") ?: 1L).toInt()
            val newQty = (currentQty + quantityToAdd).coerceAtMost(100)
            cartCol.document(product.id).update(
                mapOf(
                    "quantity" to newQty,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            val itemData = hashMapOf<String, Any?>(
                "userId" to userId,
                "productId" to product.id,
                "productName" to product.name,
                "productPrice" to product.price,
                "productImageUrl" to product.imageUrl,
                "quantity" to quantityToAdd.coerceAtLeast(1),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            cartCol.document(product.id).set(itemData).await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "users/$userId/cart")
        Result.failure(e)
    }

    suspend fun updateQuantity(userId: String, itemId: String, newQuantity: Int): Result<Unit> = try {
        val cartCol = userCartCollection(userId)
        if (newQuantity <= 0) {
            cartCol.document(itemId).delete().await()
        } else {
            cartCol.document(itemId).update(
                mapOf(
                    "quantity" to newQuantity.coerceAtMost(100),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "users/$userId/cart/$itemId")
        Result.failure(e)
    }

    suspend fun removeFromCart(userId: String, itemId: String): Result<Unit> = try {
        userCartCollection(userId).document(itemId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.DELETE, "users/$userId/cart/$itemId")
        Result.failure(e)
    }

    suspend fun clearCart(userId: String): Result<Unit> = try {
        val snapshot = userCartCollection(userId).get().await()
        val batch = db.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.DELETE, "users/$userId/cart")
        Result.failure(e)
    }
}
