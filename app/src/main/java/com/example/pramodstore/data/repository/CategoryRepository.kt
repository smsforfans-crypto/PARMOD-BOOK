package com.example.pramodstore.data.repository

import android.content.Context
import com.example.pramodstore.data.model.StoreCategory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CategoryRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(
                context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            )
        )
    )

    private val collectionPath = "categories"
    private val collection = db.collection(collectionPath)

    val defaultCategories = listOf(
        StoreCategory(id = "cat_school_books", name = "School Books", iconName = "MenuBook", order = 1),
        StoreCategory(id = "cat_notebooks", name = "Notebooks", iconName = "AutoStories", order = 2),
        StoreCategory(id = "cat_stationery", name = "Stationery", iconName = "Draw", order = 3),
        StoreCategory(id = "cat_cricket_bats", name = "Cricket Bats", iconName = "SportsCricket", order = 4),
        StoreCategory(id = "cat_cricket_acc", name = "Cricket Accessories", iconName = "Sports", order = 5),
        StoreCategory(id = "cat_sports_items", name = "Sports Items", iconName = "FitnessCenter", order = 6),
        StoreCategory(id = "cat_carrom_boards", name = "Carrom Boards", iconName = "Casino", order = 7),
        StoreCategory(id = "cat_educational", name = "Other Educational Products", iconName = "School", order = 8)
    )

    fun observeCategories(): Flow<List<StoreCategory>> = flow {
        collection.orderBy("order").snapshots()
            .map { snapshot ->
                if (snapshot.isEmpty) defaultCategories
                else snapshot.toObjects(StoreCategory::class.java)
            }
            .catch {
                emit(defaultCategories)
            }
            .collect { emit(it) }
    }

    suspend fun seedCategoriesIfEmpty(): Result<Unit> = try {
        if (com.google.firebase.Firebase.auth.currentUser == null) {
            Result.success(Unit)
        } else {
            val snapshot = collection.limit(1).get().await()
            if (snapshot.isEmpty) {
                for (cat in defaultCategories) {
                    collection.document(cat.id).set(
                        mapOf(
                            "name" to cat.name,
                            "iconName" to cat.iconName,
                            "order" to cat.order
                        )
                    ).await()
                }
            }
            Result.success(Unit)
        }
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, collectionPath)
        Result.failure(e)
    }
}
