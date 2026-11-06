package com.example.pramodstore.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val originalPrice: Double? = null,
    val description: String = "",
    val imageUrl: String = "",
    val inStock: Boolean = true,
    val stockQuantity: Int = 10,
    val rating: Double = 4.5,
    val reviewCount: Int = 12,
    val featured: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val discountPercent: Int
        get() = if (originalPrice != null && originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else {
            0
        }
}

@IgnoreExtraProperties
data class StoreCategory(
    @DocumentId val id: String = "",
    val name: String = "",
    val iconName: String = "",
    val order: Int = 0
)

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val role: String = "customer",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CartItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    val quantity: Int = 1,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WishlistItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class OrderItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = ""
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    val shippingAddress: String = "",
    val city: String = "",
    val postalCode: String = "",
    val paymentMethod: String = "COD", // "COD" or "UPI"
    val paymentStatus: String = "PENDING", // "PENDING", "VERIFIED", "FAILED"
    val upiTransactionId: String? = null,
    val orderStatus: String = "Order Placed", // "Order Placed", "Confirmed", "Processing", "Shipped", "Delivered", "Cancelled"
    val items: List<Map<String, Any>> = emptyList(),
    val totalAmount: Double = 0.0,
    val itemsCount: Int = 1,
    val orderNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AdminUser(
    @DocumentId val userId: String = "",
    val email: String = "",
    val role: String = "admin",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StoreConfig(
    @DocumentId val id: String = "store_config",
    val supportEmail: String = "rohanguptamorwabazar@gmail.com",
    val supportPhone: String = "", // Blank for now as requested
    val storeAddress: String = "PRAMOD BOOK STORE, Morwa Bazar, Samastipur, Bihar - 848121",
    val storeHours: String = "Monday - Sunday: 9:00 AM - 9:30 PM",
    val aboutText: String = "",
    val termsText: String = "",
    val privacyText: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Inquiry(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val subject: String = "",
    val message: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
