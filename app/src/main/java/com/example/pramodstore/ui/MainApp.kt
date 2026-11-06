package com.example.pramodstore.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.pramodstore.data.model.CartItem
import com.example.pramodstore.data.model.Inquiry
import com.example.pramodstore.data.model.Order
import com.example.pramodstore.data.model.Product
import com.example.pramodstore.data.model.StoreCategory
import com.example.pramodstore.data.model.StoreConfig
import com.example.pramodstore.data.model.UserProfile
import com.example.pramodstore.data.model.WishlistItem
import com.example.pramodstore.data.repository.CartRepository
import com.example.pramodstore.data.repository.CategoryRepository
import com.example.pramodstore.data.repository.OrderRepository
import com.example.pramodstore.data.repository.ProductRepository
import com.example.pramodstore.data.repository.StoreConfigRepository
import com.example.pramodstore.data.repository.UserRepository
import com.example.pramodstore.data.repository.WishlistRepository
import com.example.pramodstore.ui.components.StoreHeader
import com.example.pramodstore.ui.screens.AboutUsScreen
import com.example.pramodstore.ui.screens.AdminDashboardScreen
import com.example.pramodstore.ui.screens.AuthScreen
import com.example.pramodstore.ui.screens.CartScreen
import com.example.pramodstore.ui.screens.CategoriesScreen
import com.example.pramodstore.ui.screens.CheckoutScreen
import com.example.pramodstore.ui.screens.ContactUsScreen
import com.example.pramodstore.ui.screens.HomeScreen
import com.example.pramodstore.ui.screens.OrdersScreen
import com.example.pramodstore.ui.screens.PrivacyPolicyScreen
import com.example.pramodstore.ui.screens.ProductDetailScreen
import com.example.pramodstore.ui.screens.ProfileScreen
import com.example.pramodstore.ui.screens.TermsAndConditionsScreen
import com.example.pramodstore.ui.screens.WishlistScreen
import com.example.ui.theme.NavyPrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun MainApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isGuestMode by remember { mutableStateOf(false) }

    // Firebase Auth State Listener
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                isGuestMode = false
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null && !isGuestMode) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
                isGuestMode = false
            },
            onContinueAsGuest = {
                isGuestMode = true
            }
        )
    } else {
        StoreAppContainer(
            userId = currentUser?.uid ?: "guest_user",
            userEmail = currentUser?.email ?: "",
            userName = currentUser?.displayName ?: "Guest Visitor",
            isGuest = isGuestMode || currentUser == null,
            onSignInRequest = {
                isGuestMode = false
            },
            onSignOut = {
                Firebase.auth.signOut()
                isGuestMode = false
                val credManager = CredentialManager.create(context)
                coroutineScope.launch {
                    try {
                        credManager.clearCredentialState(ClearCredentialStateRequest())
                    } catch (_: Exception) {}
                }
            }
        )
    }
}

@Composable
fun StoreAppContainer(
    userId: String,
    userEmail: String,
    userName: String,
    isGuest: Boolean,
    onSignInRequest: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val productRepo = remember { ProductRepository(context) }
    val categoryRepo = remember { CategoryRepository(context) }
    val cartRepo = remember { CartRepository(context) }
    val wishlistRepo = remember { WishlistRepository(context) }
    val orderRepo = remember { OrderRepository(context) }
    val userRepo = remember { UserRepository(context) }
    val storeConfigRepo = remember { StoreConfigRepository(context) }

    var isAdmin by remember { mutableStateOf(!isGuest && userEmail == "hhahahhah552@gmail.com") }
    LaunchedEffect(userId, userEmail, isGuest) {
        if (!isGuest && userId.isNotBlank()) {
            isAdmin = userRepo.checkIsAdmin(userId, userEmail)
        } else {
            isAdmin = false
        }
    }

    // Seed sample bookstore catalog if empty on initial start ONLY if admin
    LaunchedEffect(isAdmin) {
        if (isAdmin) {
            productRepo.seedInitialCatalogIfEmpty()
            categoryRepo.seedCategoriesIfEmpty()
        }
    }

    // Real-time Flows (auth-gated)
    val products by productRepo.observeProducts().collectAsState(initial = productRepo.defaultProducts)
    val categories by categoryRepo.observeCategories().collectAsState(initial = categoryRepo.defaultCategories)
    val cartItems by (if (isGuest || userId.isBlank()) flowOf(emptyList()) else cartRepo.observeCart(userId)).collectAsState(initial = emptyList())
    val wishlistItems by (if (isGuest || userId.isBlank()) flowOf(emptyList()) else wishlistRepo.observeWishlist(userId)).collectAsState(initial = emptyList())
    val userOrders by (if (isGuest || userId.isBlank()) flowOf(emptyList()) else orderRepo.observeUserOrders(userId)).collectAsState(initial = emptyList())
    val allOrders by (if (isAdmin && !isGuest) orderRepo.observeAllOrders() else flowOf(emptyList())).collectAsState(initial = emptyList())
    val userProfile by (if (isGuest || userId.isBlank()) flowOf(null) else userRepo.observeProfile(userId)).collectAsState(initial = null)
    val storeConfig by storeConfigRepo.observeStoreConfig().collectAsState(initial = storeConfigRepo.defaultStoreConfig)
    val inquiries by (if (isAdmin && !isGuest) storeConfigRepo.observeInquiries() else flowOf(emptyList())).collectAsState(initial = emptyList())

    // Auto-save initial profile if not yet created and logged in
    LaunchedEffect(userId, isGuest, isAdmin) {
        if (!isGuest && userId.isNotBlank() && userProfile == null && Firebase.auth.currentUser != null) {
            userRepo.saveProfile(
                UserProfile(
                    userId = userId,
                    name = userName.ifBlank { "Valued Customer" },
                    email = userEmail,
                    role = if (isAdmin) "admin" else "customer"
                )
            )
        }
    }

    // Navigation State
    var currentScreen by remember { mutableStateOf("home") } // "home", "categories", "cart", "orders", "profile", "detail", "checkout", "wishlist", "admin", "about", "contact", "privacy", "terms"
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showGuestSignInDialog by remember { mutableStateOf(false) }
    var guestPromptAction by remember { mutableStateOf("order items") }

    val wishlistIds = remember(wishlistItems) { wishlistItems.map { it.productId }.toSet() }

    // Back handling for custom navigation
    BackHandler(enabled = currentScreen != "home") {
        when (currentScreen) {
            "checkout" -> currentScreen = "cart"
            "detail" -> currentScreen = "home"
            "wishlist" -> currentScreen = "home"
            "admin" -> currentScreen = "profile"
            "about" -> currentScreen = "profile"
            "contact" -> currentScreen = "profile"
            "privacy" -> currentScreen = "profile"
            "terms" -> currentScreen = "profile"
            else -> currentScreen = "home"
        }
    }

    Scaffold(
        topBar = {
            if (currentScreen in listOf("home", "categories")) {
                StoreHeader(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    cartItemCount = if (isGuest) 0 else cartItems.sumOf { it.quantity },
                    wishlistItemCount = if (isGuest) 0 else wishlistItems.size,
                    onCartClick = {
                        if (isGuest) {
                            guestPromptAction = "view and manage your shopping cart"
                            showGuestSignInDialog = true
                        } else {
                            currentScreen = "cart"
                        }
                    },
                    onWishlistClick = {
                        if (isGuest) {
                            guestPromptAction = "save items to your wishlist"
                            showGuestSignInDialog = true
                        } else {
                            currentScreen = "wishlist"
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentScreen in listOf("home", "categories", "cart", "orders", "profile")) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NavyPrimary, indicatorColor = Color(0xFFE3F2FD)),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "categories",
                        onClick = { currentScreen = "categories" },
                        icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                        label = { Text("Categories", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NavyPrimary, indicatorColor = Color(0xFFE3F2FD)),
                        modifier = Modifier.testTag("nav_categories")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "cart",
                        onClick = {
                            if (isGuest) {
                                guestPromptAction = "access your shopping cart and place orders"
                                showGuestSignInDialog = true
                            } else {
                                currentScreen = "cart"
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    val count = if (isGuest) 0 else cartItems.sumOf { it.quantity }
                                    if (count > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                                            Text("$count", color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        },
                        label = { Text("Cart", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NavyPrimary, indicatorColor = Color(0xFFE3F2FD)),
                        modifier = Modifier.testTag("nav_cart")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "orders",
                        onClick = {
                            if (isGuest) {
                                guestPromptAction = "view your order history and track shipments"
                                showGuestSignInDialog = true
                            } else {
                                currentScreen = "orders"
                            }
                        },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
                        label = { Text("Orders", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NavyPrimary, indicatorColor = Color(0xFFE3F2FD)),
                        modifier = Modifier.testTag("nav_orders")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "profile",
                        onClick = { currentScreen = "profile" },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(if (isGuest) "Account" else "Profile", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NavyPrimary, indicatorColor = Color(0xFFE3F2FD)),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> {
                    HomeScreen(
                        products = products,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        wishlistIds = wishlistIds,
                        isLoading = products.isEmpty(),
                        onCategorySelected = { selectedCategory = it },
                        onProductClick = {
                            selectedProduct = it
                            currentScreen = "detail"
                        },
                        onAddToCart = { product ->
                            if (isGuest) {
                                guestPromptAction = "add '${product.name}' to cart and complete checkout"
                                showGuestSignInDialog = true
                            } else {
                                coroutineScope.launch {
                                    cartRepo.addToCart(userId, product, 1)
                                    Toast.makeText(context, "${product.name} added to cart!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onToggleWishlist = { product ->
                            if (isGuest) {
                                guestPromptAction = "save '${product.name}' to your wishlist"
                                showGuestSignInDialog = true
                            } else {
                                coroutineScope.launch {
                                    val isAdded = wishlistRepo.toggleWishlist(userId, product).getOrDefault(false)
                                    val msg = if (isAdded) "Added to wishlist" else "Removed from wishlist"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }

                "categories" -> {
                    CategoriesScreen(
                        categories = categories,
                        products = products,
                        onCategoryClick = { catName ->
                            selectedCategory = catName
                            currentScreen = "home"
                        }
                    )
                }

                "cart" -> {
                    CartScreen(
                        cartItems = cartItems,
                        onUpdateQuantity = { itemId, newQty ->
                            coroutineScope.launch { cartRepo.updateQuantity(userId, itemId, newQty) }
                        },
                        onRemoveItem = { itemId ->
                            coroutineScope.launch { cartRepo.removeFromCart(userId, itemId) }
                        },
                        onProceedToCheckout = {
                            if (isGuest) {
                                guestPromptAction = "proceed to checkout"
                                showGuestSignInDialog = true
                            } else {
                                currentScreen = "checkout"
                            }
                        },
                        onStartShopping = { currentScreen = "home" }
                    )
                }

                "orders" -> {
                    OrdersScreen(
                        orders = userOrders,
                        isLoading = false,
                        onCancelOrder = { orderId ->
                            coroutineScope.launch {
                                orderRepo.cancelOrder(orderId)
                                Toast.makeText(context, "Order has been cancelled.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                "profile" -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        isAdmin = isAdmin,
                        isGuest = isGuest,
                        storeEmail = storeConfig.supportEmail,
                        storePhone = storeConfig.supportPhone,
                        onSaveProfile = { updated ->
                            coroutineScope.launch {
                                userRepo.saveProfile(updated)
                                Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onNavigateToOrders = {
                            if (isGuest) {
                                guestPromptAction = "view your orders and shipment tracking"
                                showGuestSignInDialog = true
                            } else {
                                currentScreen = "orders"
                            }
                        },
                        onNavigateToWishlist = {
                            if (isGuest) {
                                guestPromptAction = "view your saved items"
                                showGuestSignInDialog = true
                            } else {
                                currentScreen = "wishlist"
                            }
                        },
                        onNavigateToAdmin = { currentScreen = "admin" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToContact = { currentScreen = "contact" },
                        onNavigateToPrivacy = { currentScreen = "privacy" },
                        onNavigateToTerms = { currentScreen = "terms" },
                        onSignOut = onSignOut,
                        onSignInRequest = onSignInRequest
                    )
                }

                "detail" -> {
                    selectedProduct?.let { product ->
                        ProductDetailScreen(
                            product = product,
                            isWishlisted = wishlistIds.contains(product.id),
                            onBackClick = { currentScreen = "home" },
                            onAddToCart = { prod, qty ->
                                if (isGuest) {
                                    guestPromptAction = "add this item to your cart"
                                    showGuestSignInDialog = true
                                } else {
                                    coroutineScope.launch {
                                        cartRepo.addToCart(userId, prod, qty)
                                        Toast.makeText(context, "${prod.name} (x$qty) added to cart!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onBuyNow = { prod, qty ->
                                if (isGuest) {
                                    guestPromptAction = "buy this product directly"
                                    showGuestSignInDialog = true
                                } else {
                                    coroutineScope.launch {
                                        cartRepo.addToCart(userId, prod, qty)
                                        currentScreen = "checkout"
                                    }
                                }
                            },
                            onToggleWishlist = { prod ->
                                if (isGuest) {
                                    guestPromptAction = "save this item to your wishlist"
                                    showGuestSignInDialog = true
                                } else {
                                    coroutineScope.launch {
                                        wishlistRepo.toggleWishlist(userId, prod)
                                    }
                                }
                            }
                        )
                    }
                }

                "checkout" -> {
                    CheckoutScreen(
                        userProfile = userProfile,
                        cartItems = cartItems,
                        onBackClick = { currentScreen = "cart" },
                        onPlaceOrder = { newOrder ->
                            coroutineScope.launch {
                                val result = orderRepo.createOrder(newOrder)
                                if (result.isSuccess) {
                                    cartRepo.clearCart(userId)
                                    Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                                    currentScreen = "orders"
                                } else {
                                    Toast.makeText(context, "Failed to place order: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }

                "wishlist" -> {
                    WishlistScreen(
                        wishlistItems = wishlistItems,
                        onBackClick = { currentScreen = "home" },
                        onMoveToCart = { wishItem ->
                            coroutineScope.launch {
                                val prodResult = productRepo.getProductById(wishItem.productId)
                                val product = prodResult.getOrNull() ?: Product(
                                    id = wishItem.productId,
                                    name = wishItem.productName,
                                    price = wishItem.productPrice,
                                    imageUrl = wishItem.productImageUrl
                                )
                                cartRepo.addToCart(userId, product, 1)
                                wishlistRepo.toggleWishlist(userId, product)
                                Toast.makeText(context, "${wishItem.productName} moved to cart!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onRemoveFromWishlist = { prodId ->
                            coroutineScope.launch {
                                val dummy = Product(id = prodId)
                                wishlistRepo.toggleWishlist(userId, dummy)
                            }
                        }
                    )
                }

                "admin" -> {
                    AdminDashboardScreen(
                        products = products,
                        orders = allOrders,
                        categories = categories,
                        storeConfig = storeConfig,
                        inquiries = inquiries,
                        onBackClick = { currentScreen = "profile" },
                        onAddProduct = { newProd ->
                            coroutineScope.launch {
                                productRepo.addProduct(newProd)
                                Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onUpdateProduct = { updatedProd ->
                            coroutineScope.launch {
                                productRepo.updateProduct(updatedProd)
                                Toast.makeText(context, "Product updated successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDeleteProduct = { prodId ->
                            coroutineScope.launch {
                                productRepo.deleteProduct(prodId)
                                Toast.makeText(context, "Product deleted from catalog.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onUpdateOrderStatus = { orderId, newStatus ->
                            coroutineScope.launch {
                                orderRepo.updateOrderStatus(orderId, newStatus)
                                Toast.makeText(context, "Order status updated to $newStatus", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onUpdatePaymentStatus = { orderId, newStatus ->
                            coroutineScope.launch {
                                orderRepo.updatePaymentStatus(orderId, newStatus)
                                Toast.makeText(context, "Payment status updated to $newStatus", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onSeedCatalog = {
                            coroutineScope.launch {
                                productRepo.seedInitialCatalogIfEmpty()
                                categoryRepo.seedCategoriesIfEmpty()
                                Toast.makeText(context, "Bookstore catalog verified & re-seeded!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onSaveStoreConfig = { updatedConfig ->
                            coroutineScope.launch {
                                val res = storeConfigRepo.saveStoreConfig(updatedConfig)
                                if (res.isSuccess) {
                                    Toast.makeText(context, "Store contact details saved successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }

                "about" -> {
                    AboutUsScreen(
                        supportEmail = storeConfig.supportEmail,
                        storeAddress = storeConfig.storeAddress,
                        onBackClick = { currentScreen = "profile" }
                    )
                }

                "contact" -> {
                    ContactUsScreen(
                        supportEmail = storeConfig.supportEmail,
                        supportPhone = storeConfig.supportPhone,
                        storeAddress = storeConfig.storeAddress,
                        storeHours = storeConfig.storeHours,
                        onSubmitInquiry = { inquiry ->
                            storeConfigRepo.submitInquiry(inquiry)
                        },
                        onBackClick = { currentScreen = "profile" }
                    )
                }

                "privacy" -> {
                    PrivacyPolicyScreen(
                        supportEmail = storeConfig.supportEmail,
                        onBackClick = { currentScreen = "profile" }
                    )
                }

                "terms" -> {
                    TermsAndConditionsScreen(
                        supportEmail = storeConfig.supportEmail,
                        onBackClick = { currentScreen = "profile" }
                    )
                }
            }
        }
    }

    // Guest Mode Sign-In Requirement Prompt Dialog (Requirement 1)
    if (showGuestSignInDialog) {
        AlertDialog(
            onDismissRequest = { showGuestSignInDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Sign In Required",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Please sign in with your Google account or Email to $guestPromptAction. Guest mode allows browsing books, prices, and categories without signing in.",
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGuestSignInDialog = false
                        onSignInRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier.testTag("dialog_sign_in_button")
                ) {
                    Text("Sign In Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuestSignInDialog = false }) {
                    Text("Continue Browsing")
                }
            }
        )
    }
}
