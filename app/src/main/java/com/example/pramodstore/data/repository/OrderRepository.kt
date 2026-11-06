package com.example.pramodstore.data.repository

import android.content.Context
import com.example.pramodstore.data.model.Order
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class OrderRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(
                context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            )
        )
    )

    private val collectionPath = "orders"
    private val collection = db.collection(collectionPath)

    fun observeUserOrders(userId: String): Flow<List<Order>> = flow {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || userId.isBlank() || (currentUser.uid != userId && currentUser.email != "hhahahhah552@gmail.com")) {
            emit(emptyList())
            return@flow
        }
        // Client query alignment: security rules require resource.data.userId == request.auth.uid
        collection.whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Order::class.java).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, collectionPath)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    fun observeAllOrders(): Flow<List<Order>> = flow {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null || currentUser.email != "hhahahhah552@gmail.com") {
            emit(emptyList())
            return@flow
        }
        collection.snapshots()
            .map { snapshot ->
                snapshot.toObjects(Order::class.java).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, collectionPath)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun createOrder(order: Order): Result<String> = try {
        require(order.userId.isNotBlank()) { "User ID required for order" }
        val docRef = if (order.id.isNotBlank()) collection.document(order.id) else collection.document()

        val data = hashMapOf<String, Any?>(
            "userId" to order.userId,
            "customerName" to order.customerName,
            "customerEmail" to order.customerEmail,
            "customerPhone" to order.customerPhone,
            "shippingAddress" to order.shippingAddress,
            "city" to order.city,
            "postalCode" to order.postalCode,
            "paymentMethod" to order.paymentMethod,
            "paymentStatus" to order.paymentStatus,
            "upiTransactionId" to order.upiTransactionId,
            "orderStatus" to "Order Placed",
            "items" to order.items,
            "totalAmount" to order.totalAmount,
            "itemsCount" to order.itemsCount,
            "orderNotes" to order.orderNotes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        docRef.set(data).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, collectionPath)
        Result.failure(e)
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> = try {
        collection.document(orderId).update(
            mapOf(
                "orderStatus" to newStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "$collectionPath/$orderId")
        Result.failure(e)
    }

    suspend fun updatePaymentStatus(orderId: String, newStatus: String): Result<Unit> = try {
        collection.document(orderId).update(
            mapOf(
                "paymentStatus" to newStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "$collectionPath/$orderId")
        Result.failure(e)
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> = try {
        collection.document(orderId).update(
            mapOf(
                "orderStatus" to "Cancelled",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "$collectionPath/$orderId")
        Result.failure(e)
    }
}
